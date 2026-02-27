package cn.iocoder.yudao.module.system.controller.admin.im.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 邀请成员加入群聊 Request VO")
@Data
public class ConversationMemberInviteReqVO {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    @Schema(description = "被邀请的用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户列表不能为空")
    private List<Long> userIds;

}
