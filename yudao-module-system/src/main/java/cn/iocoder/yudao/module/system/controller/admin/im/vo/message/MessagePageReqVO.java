package cn.iocoder.yudao.module.system.controller.admin.im.vo.message;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 消息分页 Request VO")
@Data
public class MessagePageReqVO extends PageParam {

    @Schema(description = "所属会话ID", example = "9107")
    private Long conversationId;

    @Schema(description = "发送人用户ID", example = "114")
    private Long senderId;

    @Schema(description = "消息类型：1=文本 2=图片 3=文件 4=系统消息", example = "1")
    private Integer contentType;

    @Schema(description = "消息内容（文本存原文；图片/文件存 URL；系统消息存描述）")
    private String content;

    @Schema(description = "文件原始名称（type=3时有效）", example = "芋艿")
    private String fileName;

    @Schema(description = "文件大小字节（type=3时有效）")
    private Long fileSize;

    @Schema(description = "消息状态：0=正常 1=已撤回", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}