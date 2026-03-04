package cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "绩效打分条目明细 VO（含模板条目信息）")
@Data
public class PerformanceScoreItemDetailVO {

    @Schema(description = "打分条目ID")
    private Long id;
    @Schema(description = "关联模板条目ID")
    private Long templateItemId;
    @Schema(description = "评价指标名称")
    private String itemName;
    @Schema(description = "该条目满分")
    private BigDecimal maxScore;
    @Schema(description = "量化评价标准描述")
    private String scoringCriteria;
    @Schema(description = "佐证材料说明")
    private String evidenceDesc;
    @Schema(description = "备注")
    private String notes;
    @Schema(description = "是否固定分")
    private Boolean isFixedScore;
    @Schema(description = "区块类型：1=基础指标 2=加分项 3=扣分项")
    private Integer sectionType;
    @Schema(description = "初得分")
    private BigDecimal selfScore;
    @Schema(description = "审核分")
    private BigDecimal reviewScore;
    @Schema(description = "佐证材料URL")
    private String evidenceUrl;
    @Schema(description = "备注")
    private String remark;

}
