package cn.iocoder.yudao.module.system.dal.mysql.im;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.member.ConversationMemberPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationMemberDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import java.util.List;

/**
 * IM 会话成员 Mapper
 */
@Mapper
public interface ConversationMemberMapper extends BaseMapperX<ConversationMemberDO> {

    default PageResult<ConversationMemberDO> selectPage(ConversationMemberPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConversationMemberDO>()
                .eqIfPresent(ConversationMemberDO::getConversationId, reqVO.getConversationId())
                .eqIfPresent(ConversationMemberDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ConversationMemberDO::getMemberRole, reqVO.getMemberRole())
                .eqIfPresent(ConversationMemberDO::getLastReadMessageId, reqVO.getLastReadMessageId())
                .eqIfPresent(ConversationMemberDO::getIsMuted, reqVO.getIsMuted())
                .eqIfPresent(ConversationMemberDO::getIsPinned, reqVO.getIsPinned())
                .eqIfPresent(ConversationMemberDO::getIsDisturb, reqVO.getIsDisturb())
                .betweenIfPresent(ConversationMemberDO::getJoinTime, reqVO.getJoinTime())
                .betweenIfPresent(ConversationMemberDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ConversationMemberDO::getId));
    }

    /**
     * 查询某会话的所有成员
     */
    default List<ConversationMemberDO> selectListByConversationId(Long conversationId) {
        return selectList(new LambdaQueryWrapper<ConversationMemberDO>()
                .eq(ConversationMemberDO::getConversationId, conversationId));
    }

    /**
     * 查询某用户在某会话的成员记录
     */
    default ConversationMemberDO selectByConversationIdAndUserId(Long conversationId, Long userId) {
        return selectOne(new LambdaQueryWrapper<ConversationMemberDO>()
                .eq(ConversationMemberDO::getConversationId, conversationId)
                .eq(ConversationMemberDO::getUserId, userId));
    }

    /**
     * 计算未读消息数：id > lastReadMessageId 且 sender_id != 当前用户
     */
    @Select("SELECT COUNT(*) FROM im_message " +
            "WHERE conversation_id = #{conversationId} " +
            "AND id > #{lastReadMessageId} " +
            "AND sender_id != #{userId} " +
            "AND deleted = 0")
    Long countUnread(@Param("conversationId") Long conversationId,
                     @Param("lastReadMessageId") Long lastReadMessageId,
                     @Param("userId") Long userId);

    /**
     * 更新成员的最后已读消息ID
     */
    default void updateLastReadMessageId(Long conversationId, Long userId, Long lastReadMessageId) {
        update(null, new LambdaUpdateWrapper<ConversationMemberDO>()
                .eq(ConversationMemberDO::getConversationId, conversationId)
                .eq(ConversationMemberDO::getUserId, userId)
                .set(ConversationMemberDO::getLastReadMessageId, lastReadMessageId));
    }

    /**
     * 逻辑删除某会话内指定成员
     */
    default void deleteByConversationIdAndUserId(Long conversationId, Long userId) {
        delete(new LambdaQueryWrapper<ConversationMemberDO>()
                .eq(ConversationMemberDO::getConversationId, conversationId)
                .eq(ConversationMemberDO::getUserId, userId));
    }

}
