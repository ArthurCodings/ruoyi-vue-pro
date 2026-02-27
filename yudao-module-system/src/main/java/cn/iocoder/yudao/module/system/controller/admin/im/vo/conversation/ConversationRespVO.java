package cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - IM 会话 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ConversationRespVO {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23706")
    @ExcelProperty("会话ID")
    private Long id;

    @Schema(description = "类型：1=单聊 2=群聊", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("类型：1=单聊 2=群聊")
    private Integer type;

    @Schema(description = "会话名称（单聊为空，群聊必填）", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("会话名称（单聊为空，群聊必填）")
    private String name;

    @Schema(description = "会话头像（单聊为空，群聊可设）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("会话头像（单聊为空，群聊可设）")
    private String avatar;

    @Schema(description = "群主ID（单聊为0）", requiredMode = Schema.RequiredMode.REQUIRED, example = "30988")
    @ExcelProperty("群主ID（单聊为0）")
    private Long ownerId;

    @Schema(description = "群公告（单聊为空）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("群公告（单聊为空）")
    private String notice;

    @Schema(description = "最后一条消息ID（冗余，用于排序）", requiredMode = Schema.RequiredMode.REQUIRED, example = "25511")
    @ExcelProperty("最后一条消息ID（冗余，用于排序）")
    private Long lastMessageId;

    @Schema(description = "最后一条消息摘要（冗余，用于会话列表展示）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("最后一条消息摘要（冗余，用于会话列表展示）")
    private String lastMessageContent;

    @Schema(description = "最后一条消息时间（冗余，用于排序）")
    @ExcelProperty("最后一条消息时间（冗余，用于排序）")
    private LocalDateTime lastMessageTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}