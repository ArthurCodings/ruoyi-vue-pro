package cn.iocoder.yudao.module.system.controller.admin.im.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会话成员列表 Response VO")
@Data
public class ConversationMemberListRespVO {

    @Schema(description = "成员记录ID")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "角色：0=普通成员 1=群主 2=群管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer memberRole;

    @Schema(description = "是否被禁言")
    private Boolean isMuted;

    @Schema(description = "加入时间")
    private LocalDateTime joinTime;

}
