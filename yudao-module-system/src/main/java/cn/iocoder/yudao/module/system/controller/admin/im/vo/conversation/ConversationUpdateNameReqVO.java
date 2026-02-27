package cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 修改群聊名称 Request VO")
@Data
public class ConversationUpdateNameReqVO {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "会话ID不能为空")
    private Long id;

    @Schema(description = "新群名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发二组")
    @NotBlank(message = "群名称不能为空")
    private String name;

}
