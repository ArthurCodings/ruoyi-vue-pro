package cn.iocoder.yudao.module.system.service.im.conversationmember;

import cn.iocoder.yudao.module.system.controller.admin.im.vo.member.ConversationMemberListRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationMemberDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.im.ConversationMemberMapper;
import cn.iocoder.yudao.module.system.dal.mysql.im.MessageMapper;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CONVERSATION_MEMBER_NOT_EXISTS;

/**
 * IM 会话成员 Service 实现类
 */
@Service
@Validated
public class ConversationMemberServiceImpl implements ConversationMemberService {

    @Resource
    private ConversationMemberMapper conversationMemberMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private AdminUserService adminUserService;

    @Override
    public List<ConversationMemberListRespVO> listMembers(Long conversationId, Long currentUserId) {
        validateMember(conversationId, currentUserId);
        List<ConversationMemberDO> members = conversationMemberMapper.selectListByConversationId(conversationId);
        if (members.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> userIds = members.stream().map(ConversationMemberDO::getUserId).collect(Collectors.toList());
        Map<Long, AdminUserDO> userMap = adminUserService.getUserList(userIds).stream()
                .collect(Collectors.toMap(AdminUserDO::getId, u -> u));

        return members.stream().map(m -> {
            ConversationMemberListRespVO vo = new ConversationMemberListRespVO();
            vo.setId(m.getId());
            vo.setUserId(m.getUserId());
            vo.setMemberRole(m.getMemberRole());
            vo.setIsMuted(m.getIsMuted());
            vo.setJoinTime(m.getJoinTime());
            AdminUserDO user = userMap.get(m.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inviteMembers(Long conversationId, List<Long> userIds, Long currentUserId) {
        validateMember(conversationId, currentUserId);
        for (Long userId : userIds) {
            ConversationMemberDO exists = conversationMemberMapper.selectByConversationIdAndUserId(conversationId, userId);
            if (exists != null) {
                continue;
            }
            ConversationMemberDO member = new ConversationMemberDO();
            member.setConversationId(conversationId);
            member.setUserId(userId);
            member.setMemberRole(0);
            member.setLastReadMessageId(0L);
            member.setIsMuted(false);
            member.setIsPinned(false);
            member.setIsDisturb(false);
            member.setJoinTime(LocalDateTime.now());
            conversationMemberMapper.insert(member);
        }
    }

    @Override
    public void removeMember(Long conversationId, Long userId, Long currentUserId) {
        ConversationMemberDO operator = validateMember(conversationId, currentUserId);
        // 仅群主(1)或群管理员(2)可踢人
        if (operator.getMemberRole() == null || operator.getMemberRole() < 1) {
            throw exception(CONVERSATION_MEMBER_NOT_EXISTS);
        }
        conversationMemberMapper.deleteByConversationIdAndUserId(conversationId, userId);
    }

    @Override
    public void markRead(Long conversationId, Long currentUserId) {
        validateMember(conversationId, currentUserId);
        Long maxId = messageMapper.selectMaxIdByConversationId(conversationId);
        if (maxId == null) {
            return;
        }
        conversationMemberMapper.updateLastReadMessageId(conversationId, currentUserId, maxId);
    }

    @Override
    public ConversationMemberDO validateMember(Long conversationId, Long currentUserId) {
        ConversationMemberDO member = conversationMemberMapper.selectByConversationIdAndUserId(conversationId, currentUserId);
        if (member == null) {
            throw exception(CONVERSATION_MEMBER_NOT_EXISTS);
        }
        return member;
    }

}
