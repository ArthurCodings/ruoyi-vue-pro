package cn.iocoder.yudao.module.system.controller.admin.im.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - IM 消息新增/修改 Request VO")
@Data
public class MessageSaveReqVO {

    @Schema(description = "消息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28824")
    private Long id;

    @Schema(description = "所属会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9107")
    @NotNull(message = "所属会话ID不能为空")
    private Long conversationId;

    @Schema(description = "发送人用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "114")
    @NotNull(message = "发送人用户ID不能为空")
    private Long senderId;

    @Schema(description = "消息类型：1=文本 2=图片 3=文件 4=系统消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "消息类型：1=文本 2=图片 3=文件 4=系统消息不能为空")
    private Integer contentType;

    @Schema(description = "消息内容（文本存原文；图片/文件存 URL；系统消息存描述）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "消息内容（文本存原文；图片/文件存 URL；系统消息存描述）不能为空")
    private String content;

    @Schema(description = "文件原始名称（type=3时有效）", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "文件原始名称（type=3时有效）不能为空")
    private String fileName;

    @Schema(description = "文件大小字节（type=3时有效）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件大小字节（type=3时有效）不能为空")
    private Long fileSize;

    @Schema(description = "消息状态：0=正常 1=已撤回", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "消息状态：0=正常 1=已撤回不能为空")
    private Integer status;

}