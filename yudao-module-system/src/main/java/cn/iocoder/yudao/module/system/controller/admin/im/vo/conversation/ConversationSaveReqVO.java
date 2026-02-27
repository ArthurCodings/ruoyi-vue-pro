package cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 会话新增/修改 Request VO")
@Data
public class ConversationSaveReqVO {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23706")
    private Long id;

    @Schema(description = "类型：1=单聊 2=群聊", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "类型：1=单聊 2=群聊不能为空")
    private Integer type;

    @Schema(description = "会话名称（单聊为空，群聊必填）", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "会话名称（单聊为空，群聊必填）不能为空")
    private String name;

    @Schema(description = "会话头像（单聊为空，群聊可设）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "会话头像（单聊为空，群聊可设）不能为空")
    private String avatar;

    @Schema(description = "群主ID（单聊为0）", requiredMode = Schema.RequiredMode.REQUIRED, example = "30988")
    @NotNull(message = "群主ID（单聊为0）不能为空")
    private Long ownerId;

    @Schema(description = "群公告（单聊为空）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "群公告（单聊为空）不能为空")
    private String notice;

    @Schema(description = "最后一条消息ID（冗余，用于排序）", requiredMode = Schema.RequiredMode.REQUIRED, example = "25511")
    @NotNull(message = "最后一条消息ID（冗余，用于排序）不能为空")
    private Long lastMessageId;

    @Schema(description = "最后一条消息摘要（冗余，用于会话列表展示）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "最后一条消息摘要（冗余，用于会话列表展示）不能为空")
    private String lastMessageContent;

    @Schema(description = "最后一条消息时间（冗余，用于排序）")
    private LocalDateTime lastMessageTime;

}