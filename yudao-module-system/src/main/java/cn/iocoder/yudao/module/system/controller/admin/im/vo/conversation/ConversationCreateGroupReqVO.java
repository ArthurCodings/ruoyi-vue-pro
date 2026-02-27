package cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 创建群聊会话 Request VO")
@Data
public class ConversationCreateGroupReqVO {

    @Schema(description = "群名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发一组")
    @NotBlank(message = "群名称不能为空")
    private String name;

    @Schema(description = "群头像URL", example = "https://...")
    private String avatar;

    @Schema(description = "初始成员用户ID列表（不含创建者，创建者自动加入）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "初始成员不能为空")
    private List<Long> memberUserIds;

}
