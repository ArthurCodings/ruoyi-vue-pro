package cn.iocoder.yudao.module.system.service.im.conversation;

import cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation.ConversationCreateGroupReqVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation.ConversationListRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationDO;

import java.util.List;

/**
 * IM 会话 Service 接口
 */
public interface ConversationService {

    /**
     * 创建或获取单聊会话（A 和 B 之间有且只有一个单聊会话）
     *
     * @param currentUserId 当前用户
     * @param targetUserId  对方用户
     * @return 会话ID
     */
    Long createOrGetSingleConversation(Long currentUserId, Long targetUserId);

    /**
     * 创建群聊会话
     *
     * @param currentUserId 创建者（自动成为群主）
     * @param reqVO         群聊信息
     * @return 会话ID
     */
    Long createGroupConversation(Long currentUserId, ConversationCreateGroupReqVO reqVO);

    /**
     * 修改群聊名称（仅群主/群管理员可操作）
     *
     * @param id            会话ID
     * @param name          新名称
     * @param currentUserId 操作者
     */
    void updateConversationName(Long id, String name, Long currentUserId);

    /**
     * 解散群聊（仅群主可操作）
     *
     * @param id            会话ID
     * @param currentUserId 操作者
     */
    void dismissConversation(Long id, Long currentUserId);

    /**
     * 获取当前用户的会话列表（含未读数）
     *
     * @param currentUserId 当前用户
     * @return 会话列表
     */
    List<ConversationListRespVO> getMyConversationList(Long currentUserId);

    /**
     * 获取会话详情
     *
     * @param id 会话ID
     * @return 会话 DO
     */
    ConversationDO getConversation(Long id);

    /**
     * 更新会话冗余字段（发消息时调用）
     *
     * @param conversationId     会话ID
     * @param lastMessageId      最新消息ID
     * @param lastMessageContent 最新消息摘要
     */
    void updateLastMessage(Long conversationId, Long lastMessageId, String lastMessageContent);

}
