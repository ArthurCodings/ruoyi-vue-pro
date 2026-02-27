package cn.iocoder.yudao.module.system.controller.admin.im.vo.member;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 会话成员分页 Request VO")
@Data
public class ConversationMemberPageReqVO extends PageParam {

    @Schema(description = "会话ID", example = "1620")
    private Long conversationId;

    @Schema(description = "成员用户ID", example = "3511")
    private Long userId;

    @Schema(description = "角色：0=普通成员 1=群主 2=群管理员")
    private Integer memberRole;

    @Schema(description = "该成员最后阅读的消息ID（用于计算未读数）", example = "25066")
    private Long lastReadMessageId;

    @Schema(description = "是否被禁言")
    private Boolean isMuted;

    @Schema(description = "是否置顶")
    private Boolean isPinned;

    @Schema(description = "是否免打扰")
    private Boolean isDisturb;

    @Schema(description = "加入时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] joinTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}