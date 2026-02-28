package cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 月度薪资手动编辑 Request VO（用于填写绩效/提成）")
@Data
public class SalaryMonthlyUpdateReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "绩效")
    private BigDecimal performance;

    @Schema(description = "提成（手动录入时有效）")
    private BigDecimal commission;

    @Schema(description = "备注")
    private String remark;

}
