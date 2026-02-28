package cn.iocoder.yudao.module.infra.controller.admin.doc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 文档分类 Response VO")
@Data
public class DocCategoryRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "分类名称")
    private String name;
    @Schema(description = "父分类ID")
    private Long parentId;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "状态：0=启用 1=停用")
    private Integer status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "子分类（树形结构时使用）")
    private List<DocCategoryRespVO> children;

}
