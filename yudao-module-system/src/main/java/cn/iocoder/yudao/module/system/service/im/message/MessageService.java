package cn.iocoder.yudao.module.system.service.im.message;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageReadStatusRespVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageRespVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageSendReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.MessageDO;

/**
 * IM 消息 Service 接口
 */
public interface MessageService {

    /**
     * 发送消息
     * 1. 校验当前用户是该会话成员
     * 2. 写入 im_message
     * 3. 更新 im_conversation 冗余字段
     * 4. WebSocket 推送给在线成员
     *
     * @param reqVO         发消息请求
     * @param currentUserId 发送者
     * @return 消息ID
     */
    Long sendMessage(MessageSendReqVO reqVO, Long currentUserId);

    /**
     * 撤回消息（将 status 改为 1=已撤回）
     *
     * @param id            消息ID
     * @param currentUserId 操作者（只有发送者可撤回）
     */
    void recallMessage(Long id, Long currentUserId);

    /**
     * 分页查询会话消息列表（最新消息在前）
     *
     * @param conversationId 会话ID
     * @param pageParam      分页参数
     * @param currentUserId  当前用户（需是会话成员）
     * @return 消息分页结果
     */
    PageResult<MessageRespVO> listMessages(Long conversationId, PageParam pageParam, Long currentUserId);

    /**
     * 获取群聊消息的已读/未读详情（发送者专用）
     *
     * @param messageId     消息ID
     * @param currentUserId 当前用户（只有消息发送者可查）
     * @return 已读/未读详情
     */
    MessageReadStatusRespVO getReadStatus(Long messageId, Long currentUserId);

    /**
     * 获取消息详情
     *
     * @param id 消息ID
     * @return 消息 DO
     */
    MessageDO getMessage(Long id);

}
