package cn.iocoder.yudao.module.system.service.im.message;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.api.websocket.WebSocketSenderApi;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageReadStatusRespVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageRespVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageSendReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationDO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationMemberDO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.MessageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.im.ConversationMemberMapper;
import cn.iocoder.yudao.module.system.dal.mysql.im.MessageMapper;
import cn.iocoder.yudao.module.system.service.im.conversation.ConversationService;
import cn.iocoder.yudao.module.system.service.im.conversationmember.ConversationMemberService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.MESSAGE_NOT_EXISTS;

/**
 * IM 消息 Service 实现类
 */
@Service
@Validated
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private ConversationMemberMapper conversationMemberMapper;

    @Resource
    @Lazy
    private ConversationService conversationService;

    @Resource
    @Lazy
    private ConversationMemberService conversationMemberService;

    @Resource
    private AdminUserService adminUserService;

    @Autowired(required = false)
    private WebSocketSenderApi webSocketSenderApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(MessageSendReqVO reqVO, Long currentUserId) {
        // 校验：当前用户必须是该会话成员
        conversationMemberService.validateMember(reqVO.getConversationId(), currentUserId);

        // 写入消息
        MessageDO message = new MessageDO();
        message.setConversationId(reqVO.getConversationId());
        message.setSenderId(currentUserId);
        message.setContentType(reqVO.getContentType());
        message.setContent(reqVO.getContent());
        message.setFileName(reqVO.getFileName() != null ? reqVO.getFileName() : "");
        message.setFileSize(reqVO.getFileSize() != null ? reqVO.getFileSize() : 0L);
        message.setStatus(0);
        messageMapper.insert(message);

        // 更新会话冗余字段
        String summary = buildSummary(reqVO.getContentType(), reqVO.getContent(), reqVO.getFileName());
        conversationService.updateLastMessage(reqVO.getConversationId(), message.getId(), summary);

        // WebSocket 推送给所有在线成员（排除发送者）
        if (webSocketSenderApi != null) {
            MessageRespVO respVO = BeanUtils.toBean(message, MessageRespVO.class);
            AdminUserDO sender = adminUserService.getUser(currentUserId);
            if (sender != null) {
                respVO.setSenderNickname(sender.getNickname());
                respVO.setSenderAvatar(sender.getAvatar());
            }
            List<ConversationMemberDO> members = conversationMemberMapper.selectListByConversationId(reqVO.getConversationId());
            members.stream()
                    .filter(m -> !m.getUserId().equals(currentUserId))
                    .forEach(m -> webSocketSenderApi.sendObject(
                            UserTypeEnum.ADMIN.getValue(),
                            m.getUserId(),
                            "im-message",
                            respVO));
        }
        return message.getId();
    }

    @Override
    public void recallMessage(Long id, Long currentUserId) {
        MessageDO message = messageMapper.selectById(id);
        if (message == null) {
            throw exception(MESSAGE_NOT_EXISTS);
        }
        // 仅发送者可撤回
        if (!message.getSenderId().equals(currentUserId)) {
            throw exception(MESSAGE_NOT_EXISTS);
        }
        messageMapper.update(null, new LambdaUpdateWrapper<MessageDO>()
                .eq(MessageDO::getId, id)
                .set(MessageDO::getStatus, 1));
    }

    @Override
    public PageResult<MessageRespVO> listMessages(Long conversationId, PageParam pageParam, Long currentUserId) {
        conversationMemberService.validateMember(conversationId, currentUserId);
        PageResult<MessageDO> pageResult = messageMapper.selectPageByConversationId(conversationId, pageParam);

        // 补充发送者信息
        List<Long> senderIds = pageResult.getList().stream()
                .map(MessageDO::getSenderId).distinct().collect(Collectors.toList());
        Map<Long, AdminUserDO> userMap = adminUserService.getUserList(senderIds).stream()
                .collect(Collectors.toMap(AdminUserDO::getId, u -> u));

        List<MessageRespVO> voList = pageResult.getList().stream().map(m -> {
            MessageRespVO vo = BeanUtils.toBean(m, MessageRespVO.class);
            AdminUserDO sender = userMap.get(m.getSenderId());
            if (sender != null) {
                vo.setSenderNickname(sender.getNickname());
                vo.setSenderAvatar(sender.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    public MessageReadStatusRespVO getReadStatus(Long messageId, Long currentUserId) {
        MessageDO message = messageMapper.selectById(messageId);
        if (message == null) {
            throw exception(MESSAGE_NOT_EXISTS);
        }
        Long conversationId = message.getConversationId();

        // 查该会话所有成员（排除发送者）
        List<ConversationMemberDO> members = conversationMemberMapper.selectListByConversationId(conversationId)
                .stream().filter(m -> !m.getUserId().equals(message.getSenderId()))
                .collect(Collectors.toList());

        List<Long> memberUserIds = members.stream().map(ConversationMemberDO::getUserId).collect(Collectors.toList());
        Map<Long, AdminUserDO> userMap = new java.util.HashMap<>();
        if (!memberUserIds.isEmpty()) {
            adminUserService.getUserList(memberUserIds).forEach(u -> userMap.put(u.getId(), u));
        }

        List<MessageReadStatusRespVO.UserSimpleVO> readList = new ArrayList<>();
        List<MessageReadStatusRespVO.UserSimpleVO> unreadList = new ArrayList<>();

        for (ConversationMemberDO m : members) {
            MessageReadStatusRespVO.UserSimpleVO userVO = new MessageReadStatusRespVO.UserSimpleVO();
            userVO.setUserId(m.getUserId());
            AdminUserDO user = userMap.get(m.getUserId());
            if (user != null) {
                userVO.setNickname(user.getNickname());
                userVO.setAvatar(user.getAvatar());
            }
            // 该成员 lastReadMessageId >= 消息ID → 已读
            if (m.getLastReadMessageId() != null && m.getLastReadMessageId() >= messageId) {
                readList.add(userVO);
            } else {
                unreadList.add(userVO);
            }
        }

        MessageReadStatusRespVO result = new MessageReadStatusRespVO();
        result.setReadCount(readList.size());
        result.setUnreadCount(unreadList.size());
        result.setTotalCount(members.size());
        result.setReadUserList(readList);
        result.setUnreadUserList(unreadList);
        return result;
    }

    @Override
    public MessageDO getMessage(Long id) {
        return messageMapper.selectById(id);
    }

    private String buildSummary(Integer contentType, String content, String fileName) {
        if (contentType == null) return content;
        return switch (contentType) {
            case 2 -> "[图片]";
            case 3 -> "[文件] " + (fileName != null ? fileName : "");
            case 4 -> "[系统消息]";
            default -> content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content;
        };
    }

}
