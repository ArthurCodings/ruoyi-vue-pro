package cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 会话分页 Request VO")
@Data
public class ConversationPageReqVO extends PageParam {

    @Schema(description = "类型：1=单聊 2=群聊", example = "2")
    private Integer type;

    @Schema(description = "会话名称（单聊为空，群聊必填）", example = "赵六")
    private String name;

    @Schema(description = "会话头像（单聊为空，群聊可设）")
    private String avatar;

    @Schema(description = "群主ID（单聊为0）", example = "30988")
    private Long ownerId;

    @Schema(description = "群公告（单聊为空）")
    private String notice;

    @Schema(description = "最后一条消息ID（冗余，用于排序）", example = "25511")
    private Long lastMessageId;

    @Schema(description = "最后一条消息摘要（冗余，用于会话列表展示）")
    private String lastMessageContent;

    @Schema(description = "最后一条消息时间（冗余，用于排序）")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastMessageTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}