package cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "绩效评分模板 创建/修改 Request VO")
@Data
public class PerformanceTemplateSaveReqVO {

    @Schema(description = "模板ID（修改时必填）")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模板名称不能为空")
    private String name;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "绩效薪酬基准系数（默认0.45，即绩效薪酬=基本薪资×此系数×绩效系数）")
    private BigDecimal performanceBaseRatio;

    @Schema(description = "分数-绩效系数映射规则JSON（不填则使用默认规则）")
    private String coefficientRules;

    @Schema(description = "状态：0=启用 1=停用")
    private Integer status;

    @Schema(description = "评分区块列表（含条目）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评分区块不能为空")
    private List<PerformanceTemplateSectionVO> sections;

    @Schema(description = "应用此模板的员工用户ID列表")
    private List<Long> userIds;

}
