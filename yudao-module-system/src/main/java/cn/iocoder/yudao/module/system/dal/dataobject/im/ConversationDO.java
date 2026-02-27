package cn.iocoder.yudao.module.system.dal.dataobject.im;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * IM 会话 DO
 *
 * @author Arthur
 */
@TableName("im_conversation")
@KeySequence("im_conversation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDO extends BaseDO {

    /**
     * 会话ID
     */
    @TableId
    private Long id;
    /**
     * 类型：1=单聊 2=群聊
     */
    private Integer type;
    /**
     * 会话名称（单聊为空，群聊必填）
     */
    private String name;
    /**
     * 会话头像（单聊为空，群聊可设）
     */
    private String avatar;
    /**
     * 群主ID（单聊为0）
     */
    private Long ownerId;
    /**
     * 群公告（单聊为空）
     */
    private String notice;
    /**
     * 最后一条消息ID（冗余，用于排序）
     */
    private Long lastMessageId;
    /**
     * 最后一条消息摘要（冗余，用于会话列表展示）
     */
    private String lastMessageContent;
    /**
     * 最后一条消息时间（冗余，用于排序）
     */
    private LocalDateTime lastMessageTime;


}