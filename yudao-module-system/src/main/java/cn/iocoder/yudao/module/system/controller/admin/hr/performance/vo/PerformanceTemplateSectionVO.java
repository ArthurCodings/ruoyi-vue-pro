package cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "绩效模板区块 VO（嵌套在模板中）")
@Data
public class PerformanceTemplateSectionVO {

    @Schema(description = "区块ID（修改时必填，新增时不填）")
    private Long id;
    @Schema(description = "区块名称（如：一、基础指标（100分））", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sectionName;
    @Schema(description = "区块类型：1=基础指标 2=加分项 3=扣分项", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sectionType;
    @Schema(description = "区块最高分", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal maxScore;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "条目列表")
    private List<PerformanceTemplateItemVO> items;

}
