package cn.iocoder.yudao.module.system.controller.admin.im.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 会话成员新增/修改 Request VO")
@Data
public class ConversationMemberSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31608")
    private Long id;

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1620")
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    @Schema(description = "成员用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3511")
    @NotNull(message = "成员用户ID不能为空")
    private Long userId;

    @Schema(description = "角色：0=普通成员 1=群主 2=群管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色：0=普通成员 1=群主 2=群管理员不能为空")
    private Integer memberRole;

    @Schema(description = "该成员最后阅读的消息ID（用于计算未读数）", requiredMode = Schema.RequiredMode.REQUIRED, example = "25066")
    @NotNull(message = "该成员最后阅读的消息ID（用于计算未读数）不能为空")
    private Long lastReadMessageId;

    @Schema(description = "是否被禁言", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否被禁言不能为空")
    private Boolean isMuted;

    @Schema(description = "是否置顶", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否置顶不能为空")
    private Boolean isPinned;

    @Schema(description = "是否免打扰", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否免打扰不能为空")
    private Boolean isDisturb;

    @Schema(description = "加入时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "加入时间不能为空")
    private LocalDateTime joinTime;

}