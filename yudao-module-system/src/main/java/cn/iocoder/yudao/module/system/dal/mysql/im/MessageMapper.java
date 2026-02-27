package cn.iocoder.yudao.module.system.dal.mysql.im;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessagePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.MessageDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * IM 消息 Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapperX<MessageDO> {

    default PageResult<MessageDO> selectPage(MessagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageDO>()
                .eqIfPresent(MessageDO::getConversationId, reqVO.getConversationId())
                .eqIfPresent(MessageDO::getSenderId, reqVO.getSenderId())
                .eqIfPresent(MessageDO::getContentType, reqVO.getContentType())
                .eqIfPresent(MessageDO::getContent, reqVO.getContent())
                .likeIfPresent(MessageDO::getFileName, reqVO.getFileName())
                .eqIfPresent(MessageDO::getFileSize, reqVO.getFileSize())
                .eqIfPresent(MessageDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MessageDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessageDO::getId));
    }

    /**
     * 按会话分页查询消息（按消息ID倒序，即最新消息在前）
     */
    default PageResult<MessageDO> selectPageByConversationId(Long conversationId, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapper<MessageDO>()
                .eq(MessageDO::getConversationId, conversationId)
                .orderByDesc(MessageDO::getId));
    }

    /**
     * 查询会话内最大消息ID（用于标记全部已读）
     */
    @Select("SELECT MAX(id) FROM im_message WHERE conversation_id = #{conversationId} AND deleted = 0")
    Long selectMaxIdByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 查询消息状态不为已撤回的消息列表（按会话ID）
     */
    default List<MessageDO> selectListByConversationId(Long conversationId) {
        return selectList(new LambdaQueryWrapper<MessageDO>()
                .eq(MessageDO::getConversationId, conversationId)
                .orderByAsc(MessageDO::getId));
    }

}
