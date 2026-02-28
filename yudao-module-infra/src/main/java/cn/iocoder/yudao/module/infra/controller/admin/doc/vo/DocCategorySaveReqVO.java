package cn.iocoder.yudao.module.infra.controller.admin.doc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 文档分类创建/修改 Request VO")
@Data
public class DocCategorySaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(description = "父分类ID（0=根分类）")
    private Long parentId;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：0=启用 1=停用")
    private Integer status;

}
