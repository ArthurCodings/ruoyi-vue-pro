package cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "绩效模板条目 VO（嵌套在区块中）")
@Data
public class PerformanceTemplateItemVO {

    @Schema(description = "条目ID（修改时必填，新增时不填）")
    private Long id;
    @Schema(description = "评价指标名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemName;
    @Schema(description = "该条目满分", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal maxScore;
    @Schema(description = "量化评价标准描述")
    private String scoringCriteria;
    @Schema(description = "佐证材料说明")
    private String evidenceDesc;
    @Schema(description = "备注（如：重大差错本项得0分）")
    private String notes;
    @Schema(description = "是否为固定分（true=系统自动计算，如全勤固定2分）")
    private Boolean isFixedScore;
    @Schema(description = "固定分值条件说明")
    private String fixedScoreCondition;
    @Schema(description = "排序")
    private Integer sort;

}
