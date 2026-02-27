package cn.iocoder.yudao.module.system.controller.admin.im.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - IM 消息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MessageRespVO {

    @Schema(description = "消息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28824")
    @ExcelProperty("消息ID")
    private Long id;

    @Schema(description = "所属会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9107")
    @ExcelProperty("所属会话ID")
    private Long conversationId;

    @Schema(description = "发送人用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "114")
    @ExcelProperty("发送人用户ID")
    private Long senderId;

    @Schema(description = "消息类型：1=文本 2=图片 3=文件 4=系统消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("消息类型：1=文本 2=图片 3=文件 4=系统消息")
    private Integer contentType;

    @Schema(description = "消息内容（文本存原文；图片/文件存 URL；系统消息存描述）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("消息内容（文本存原文；图片/文件存 URL；系统消息存描述）")
    private String content;

    @Schema(description = "文件原始名称（type=3时有效）", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("文件原始名称（type=3时有效）")
    private String fileName;

    @Schema(description = "文件大小字节（type=3时有效）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("文件大小字节（type=3时有效）")
    private Long fileSize;

    @Schema(description = "消息状态：0=正常 1=已撤回", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("消息状态：0=正常 1=已撤回")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "发送者昵称")
    private String senderNickname;

    @Schema(description = "发送者头像")
    private String senderAvatar;

}
