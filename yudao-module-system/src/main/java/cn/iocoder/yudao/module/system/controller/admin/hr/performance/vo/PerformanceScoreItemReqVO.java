package cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "绩效打分条目 Request VO（嵌套在打分记录中）")
@Data
public class PerformanceScoreItemReqVO {

    @Schema(description = "关联模板条目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "模板条目ID不能为空")
    private Long templateItemId;

    @Schema(description = "区块类型：1=基础指标 2=加分项 3=扣分项")
    private Integer sectionType;

    @Schema(description = "初得分（被考核人自报）")
    private BigDecimal selfScore;

    @Schema(description = "审核分（考核人审定）")
    private BigDecimal reviewScore;

    @Schema(description = "佐证材料URL（多文件逗号分隔）")
    private String evidenceUrl;

    @Schema(description = "备注")
    private String remark;

}
