package cn.iocoder.yudao.module.system.service.im.conversation;

import cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation.ConversationCreateGroupReqVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation.ConversationListRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationDO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationMemberDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.im.ConversationMapper;
import cn.iocoder.yudao.module.system.dal.mysql.im.ConversationMemberMapper;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CONVERSATION_NOT_EXISTS;

/**
 * IM 会话 Service 实现类
 */
@Service
@Validated
public class ConversationServiceImpl implements ConversationService {

    @Resource
    private ConversationMapper conversationMapper;

    @Resource
    private ConversationMemberMapper conversationMemberMapper;

    @Resource
    @Lazy
    private AdminUserService adminUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrGetSingleConversation(Long currentUserId, Long targetUserId) {
        Long existingId = conversationMapper.selectSingleConversationId(currentUserId, targetUserId);
        if (existingId != null) {
            return existingId;
        }
        // 新建单聊会话
        ConversationDO conversation = new ConversationDO();
        conversation.setType(1);
        conversation.setName("");
        conversation.setAvatar("");
        conversation.setOwnerId(0L);
        conversation.setNotice("");
        conversation.setLastMessageId(0L);
        conversation.setLastMessageContent("");
        conversationMapper.insert(conversation);

        // 插入两条成员记录
        insertMember(conversation.getId(), currentUserId, 0);
        insertMember(conversation.getId(), targetUserId, 0);

        return conversation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGroupConversation(Long currentUserId, ConversationCreateGroupReqVO reqVO) {
        ConversationDO conversation = new ConversationDO();
        conversation.setType(2);
        conversation.setName(reqVO.getName());
        conversation.setAvatar(reqVO.getAvatar() != null ? reqVO.getAvatar() : "");
        conversation.setOwnerId(currentUserId);
        conversation.setNotice("");
        conversation.setLastMessageId(0L);
        conversation.setLastMessageContent("");
        conversationMapper.insert(conversation);

        // 创建者为群主
        insertMember(conversation.getId(), currentUserId, 1);
        // 其他成员
        for (Long memberId : reqVO.getMemberUserIds()) {
            if (!memberId.equals(currentUserId)) {
                insertMember(conversation.getId(), memberId, 0);
            }
        }
        return conversation.getId();
    }

    @Override
    public void updateConversationName(Long id, String name, Long currentUserId) {
        ConversationDO conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            throw exception(CONVERSATION_NOT_EXISTS);
        }
        conversationMapper.updateById(ConversationDO.builder()
                .id(id)
                .name(name)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dismissConversation(Long id, Long currentUserId) {
        ConversationDO conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            throw exception(CONVERSATION_NOT_EXISTS);
        }
        // 逻辑删除会话及所有成员记录
        conversationMapper.deleteById(id);
        List<ConversationMemberDO> members = conversationMemberMapper.selectListByConversationId(id);
        members.forEach(m -> conversationMemberMapper.deleteById(m.getId()));
    }

    @Override
    public List<ConversationListRespVO> getMyConversationList(Long currentUserId) {
        List<ConversationDO> conversations = conversationMapper.selectListByUserId(currentUserId);
        if (conversations.isEmpty()) {
            return new ArrayList<>();
        }
        // 收集需要查用户信息的 userId（单聊对方）
        List<Long> allMemberConversationIds = conversations.stream()
                .map(ConversationDO::getId).collect(Collectors.toList());

        // 批量查当前用户的所有成员记录（用于未读数 + 置顶 + 免打扰）
        List<ConversationMemberDO> myMemberRecords = conversationMemberMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ConversationMemberDO>()
                        .eq(ConversationMemberDO::getUserId, currentUserId)
                        .in(ConversationMemberDO::getConversationId, allMemberConversationIds));
        Map<Long, ConversationMemberDO> myMemberMap = myMemberRecords.stream()
                .collect(Collectors.toMap(ConversationMemberDO::getConversationId, m -> m));

        // 对于单聊，需要找到对方的用户信息
        // 查出所有单聊会话的所有成员（排除自己）
        List<ConversationDO> singleConversations = conversations.stream()
                .filter(c -> c.getType() == 1).collect(Collectors.toList());

        // 构建单聊会话ID -> 对方userId 的映射
        Map<Long, Long> singleConvOtherUserIdMap = new java.util.HashMap<>();
        if (!singleConversations.isEmpty()) {
            List<Long> singleIds = singleConversations.stream().map(ConversationDO::getId).collect(Collectors.toList());
            List<ConversationMemberDO> allSingleMembers = conversationMemberMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ConversationMemberDO>()
                            .in(ConversationMemberDO::getConversationId, singleIds)
                            .ne(ConversationMemberDO::getUserId, currentUserId));
            allSingleMembers.forEach(m -> singleConvOtherUserIdMap.put(m.getConversationId(), m.getUserId()));
        }

        // 批量查用户信息
        List<Long> otherUserIds = new ArrayList<>(singleConvOtherUserIdMap.values());
        Map<Long, AdminUserDO> userMap = new java.util.HashMap<>();
        if (!otherUserIds.isEmpty()) {
            adminUserService.getUserList(otherUserIds)
                    .forEach(u -> userMap.put(u.getId(), u));
        }

        // 组装结果
        return conversations.stream().map(c -> {
            ConversationListRespVO vo = new ConversationListRespVO();
            vo.setId(c.getId());
            vo.setType(c.getType());
            vo.setOwnerId(c.getOwnerId());
            vo.setNotice(c.getNotice());
            vo.setLastMessageContent(c.getLastMessageContent());
            vo.setLastMessageTime(c.getLastMessageTime());
            vo.setCreateTime(c.getCreateTime());

            ConversationMemberDO myMember = myMemberMap.get(c.getId());
            vo.setIsPinned(myMember != null && Boolean.TRUE.equals(myMember.getIsPinned()));
            vo.setIsDisturb(myMember != null && Boolean.TRUE.equals(myMember.getIsDisturb()));

            // 未读数
            if (myMember != null) {
                Long unread = conversationMemberMapper.countUnread(
                        c.getId(), myMember.getLastReadMessageId(), currentUserId);
                vo.setUnreadCount(unread != null ? unread : 0L);
            } else {
                vo.setUnreadCount(0L);
            }

            // 单聊：名称/头像取对方
            if (c.getType() == 1) {
                Long otherUserId = singleConvOtherUserIdMap.get(c.getId());
                vo.setOtherUserId(otherUserId);
                if (otherUserId != null) {
                    AdminUserDO other = userMap.get(otherUserId);
                    vo.setName(other != null ? other.getNickname() : "");
                    vo.setAvatar(other != null ? other.getAvatar() : "");
                }
            } else {
                vo.setName(c.getName());
                vo.setAvatar(c.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public ConversationDO getConversation(Long id) {
        return conversationMapper.selectById(id);
    }

    @Override
    public void updateLastMessage(Long conversationId, Long lastMessageId, String lastMessageContent) {
        conversationMapper.update(null, new LambdaUpdateWrapper<ConversationDO>()
                .eq(ConversationDO::getId, conversationId)
                .set(ConversationDO::getLastMessageId, lastMessageId)
                .set(ConversationDO::getLastMessageContent, lastMessageContent)
                .set(ConversationDO::getLastMessageTime, LocalDateTime.now()));
    }

    private void insertMember(Long conversationId, Long userId, int memberRole) {
        ConversationMemberDO member = new ConversationMemberDO();
        member.setConversationId(conversationId);
        member.setUserId(userId);
        member.setMemberRole(memberRole);
        member.setLastReadMessageId(0L);
        member.setIsMuted(false);
        member.setIsPinned(false);
        member.setIsDisturb(false);
        member.setJoinTime(LocalDateTime.now());
        conversationMemberMapper.insert(member);
    }

}
