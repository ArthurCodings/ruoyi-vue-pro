package cn.iocoder.yudao.module.infra.controller.admin.doc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 在线文档创建/修改 Request VO")
@Data
public class InfraDocSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "所属分类ID")
    private Long categoryId;

    @Schema(description = "文档名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文档名称不能为空")
    private String name;

    @Schema(description = "文档类型：1=上传文件预览 2=外部链接 3=下载附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文档类型不能为空")
    private Integer type;

    @Schema(description = "文件URL（type=1/3时）")
    private String fileUrl;

    @Schema(description = "文件扩展名（docx/xlsx/pdf）")
    private String fileType;

    @Schema(description = "外部链接（type=2时）")
    private String externalUrl;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：0=公开 1=仅内部")
    private Integer status;

    @Schema(description = "简介说明")
    private String remark;

}
