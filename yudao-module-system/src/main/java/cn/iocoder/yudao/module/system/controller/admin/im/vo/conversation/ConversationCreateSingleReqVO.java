package cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 创建单聊会话 Request VO")
@Data
public class ConversationCreateSingleReqVO {

    @Schema(description = "对方用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "对方用户ID不能为空")
    private Long targetUserId;

}
