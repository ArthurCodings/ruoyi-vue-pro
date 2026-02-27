package cn.iocoder.yudao.module.system.controller.admin.im.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 发送 IM 消息 Request VO")
@Data
public class MessageSendReqVO {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    @Schema(description = "消息类型：1=文本 2=图片 3=文件 4=系统消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "消息类型不能为空")
    private Integer contentType;

    @Schema(description = "消息内容（文本存原文；图片/文件存 URL；系统消息存描述）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息内容不能为空")
    private String content;

    @Schema(description = "文件原始名称（contentType=3 时有效）", example = "合同.pdf")
    private String fileName;

    @Schema(description = "文件大小字节（contentType=3 时有效）", example = "102400")
    private Long fileSize;

}
