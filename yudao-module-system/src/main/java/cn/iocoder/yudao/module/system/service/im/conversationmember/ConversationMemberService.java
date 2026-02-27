package cn.iocoder.yudao.module.system.service.im.conversationmember;

import cn.iocoder.yudao.module.system.controller.admin.im.vo.member.ConversationMemberListRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationMemberDO;

import java.util.List;

/**
 * IM 会话成员 Service 接口
 */
public interface ConversationMemberService {

    /**
     * 查询群聊成员列表（当前用户必须是该会话成员）
     *
     * @param conversationId 会话ID
     * @param currentUserId  当前用户
     * @return 成员列表
     */
    List<ConversationMemberListRespVO> listMembers(Long conversationId, Long currentUserId);

    /**
     * 邀请成员加入群聊
     *
     * @param conversationId 会话ID
     * @param userIds        被邀请的用户ID列表
     * @param currentUserId  操作者
     */
    void inviteMembers(Long conversationId, List<Long> userIds, Long currentUserId);

    /**
     * 踢出成员（仅群主/群管理员可操作）
     *
     * @param conversationId 会话ID
     * @param userId         被踢用户ID
     * @param currentUserId  操作者
     */
    void removeMember(Long conversationId, Long userId, Long currentUserId);

    /**
     * 标记该会话消息全部已读（将 last_read_message_id 更新为最新消息ID）
     *
     * @param conversationId 会话ID
     * @param currentUserId  当前用户
     */
    void markRead(Long conversationId, Long currentUserId);

    /**
     * 校验当前用户是否是该会话的成员，不是则抛出异常
     *
     * @param conversationId 会话ID
     * @param currentUserId  当前用户
     * @return 成员记录
     */
    ConversationMemberDO validateMember(Long conversationId, Long currentUserId);

}
