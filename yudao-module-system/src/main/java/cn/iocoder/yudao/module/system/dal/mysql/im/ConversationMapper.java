package cn.iocoder.yudao.module.system.dal.mysql.im;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationDO;
import cn.iocoder.yudao.module.system.dal.dataobject.im.ConversationMemberDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * IM 会话 Mapper
 */
@Mapper
public interface ConversationMapper extends BaseMapperX<ConversationDO> {

    /**
     * 查找两个用户之间的单聊会话（type=1 且同时包含这两个成员）
     */
    @Select("SELECT c.id FROM im_conversation c " +
            "JOIN im_conversation_member m1 ON c.id = m1.conversation_id AND m1.user_id = #{userId1} AND m1.deleted = 0 " +
            "JOIN im_conversation_member m2 ON c.id = m2.conversation_id AND m2.user_id = #{userId2} AND m2.deleted = 0 " +
            "WHERE c.type = 1 AND c.deleted = 0 " +
            "LIMIT 1")
    Long selectSingleConversationId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 查询指定用户参与的所有会话（关联成员表，按置顶+最后消息时间排序）
     */
    @Select("SELECT c.* FROM im_conversation c " +
            "JOIN im_conversation_member m ON c.id = m.conversation_id AND m.user_id = #{userId} AND m.deleted = 0 " +
            "WHERE c.deleted = 0 " +
            "ORDER BY m.is_pinned DESC, c.last_message_time DESC")
    List<ConversationDO> selectListByUserId(@Param("userId") Long userId);

}
