package cn.iocoder.yudao.module.system.controller.admin.im.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - IM 会话成员 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ConversationMemberRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31608")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1620")
    @ExcelProperty("会话ID")
    private Long conversationId;

    @Schema(description = "成员用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3511")
    @ExcelProperty("成员用户ID")
    private Long userId;

    @Schema(description = "角色：0=普通成员 1=群主 2=群管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("角色：0=普通成员 1=群主 2=群管理员")
    private Integer memberRole;

    @Schema(description = "该成员最后阅读的消息ID（用于计算未读数）", requiredMode = Schema.RequiredMode.REQUIRED, example = "25066")
    @ExcelProperty("该成员最后阅读的消息ID（用于计算未读数）")
    private Long lastReadMessageId;

    @Schema(description = "是否被禁言", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否被禁言")
    private Boolean isMuted;

    @Schema(description = "是否置顶", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否置顶")
    private Boolean isPinned;

    @Schema(description = "是否免打扰", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否免打扰")
    private Boolean isDisturb;

    @Schema(description = "加入时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("加入时间")
    private LocalDateTime joinTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}