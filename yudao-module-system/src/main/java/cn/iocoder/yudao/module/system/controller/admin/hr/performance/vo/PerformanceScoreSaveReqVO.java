package cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "绩效打分记录 创建/修改 Request VO")
@Data
public class PerformanceScoreSaveReqVO {

    @Schema(description = "打分记录ID（修改时必填）")
    private Long id;

    @Schema(description = "被考核员工用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "被考核员工不能为空")
    private Long userId;

    @Schema(description = "考核年月（如202603）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "考核年月不能为空")
    private Integer yearMonth;

    @Schema(description = "考勤扣分（由人事部门填写，对应绩效表中的考勤扣分项）")
    private BigDecimal attendanceDeductionScore;

    @Schema(description = "打分条目列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "打分条目不能为空")
    private List<PerformanceScoreItemReqVO> items;

    @Schema(description = "审核意见（审核时填写）")
    private String reviewComment;

}
