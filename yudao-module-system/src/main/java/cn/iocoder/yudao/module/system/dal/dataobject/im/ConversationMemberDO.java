package cn.iocoder.yudao.module.system.dal.dataobject.im;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * IM 会话成员 DO
 *
 * @author Arthur
 */
@TableName("im_conversation_member")
@KeySequence("im_conversation_member_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMemberDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 会话ID
     */
    private Long conversationId;
    /**
     * 成员用户ID
     */
    private Long userId;
    /**
     * 角色：0=普通成员 1=群主 2=群管理员
     */
    private Integer memberRole;
    /**
     * 该成员最后阅读的消息ID（用于计算未读数）
     */
    private Long lastReadMessageId;
    /**
     * 是否被禁言
     */
    private Boolean isMuted;
    /**
     * 是否置顶
     */
    private Boolean isPinned;
    /**
     * 是否免打扰
     */
    private Boolean isDisturb;
    /**
     * 加入时间
     */
    private LocalDateTime joinTime;


}