package cn.iocoder.yudao.module.system.dal.dataobject.im;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * IM 消息 DO
 *
 * @author Arthur
 */
@TableName("im_message")
@KeySequence("im_message_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDO extends BaseDO {

    /**
     * 消息ID
     */
    @TableId
    private Long id;
    /**
     * 所属会话ID
     */
    private Long conversationId;
    /**
     * 发送人用户ID
     */
    private Long senderId;
    /**
     * 消息类型：1=文本 2=图片 3=文件 4=系统消息
     */
    private Integer contentType;
    /**
     * 消息内容（文本存原文；图片/文件存 URL；系统消息存描述）
     */
    private String content;
    /**
     * 文件原始名称（type=3时有效）
     */
    private String fileName;
    /**
     * 文件大小字节（type=3时有效）
     */
    private Long fileSize;
    /**
     * 消息状态：0=正常 1=已撤回
     */
    private Integer status;


}