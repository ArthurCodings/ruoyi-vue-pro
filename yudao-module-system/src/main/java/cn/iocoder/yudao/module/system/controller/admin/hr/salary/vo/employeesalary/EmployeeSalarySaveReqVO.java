package cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 员工薪资档案创建/修改 Request VO")
@Data
public class EmployeeSalarySaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "员工用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "员工不能为空")
    private Long userId;

    @Schema(description = "基本工资（元）")
    private BigDecimal baseSalary;

    @Schema(description = "岗位工资（元）")
    private BigDecimal positionSalary;

    @Schema(description = "补贴（元）")
    private BigDecimal allowance;

    @Schema(description = "生效日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "生效日期不能为空")
    private LocalDate effectiveDate;

    @Schema(description = "备注（如调薪原因）")
    private String remark;

}
