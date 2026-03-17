package cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 员工薪资档案 Response VO")
@Data
public class EmployeeSalaryRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "员工用户ID")
    private Long userId;
    @Schema(description = "员工姓名（用于前端展示）")
    private String nickname;
    @Schema(description = "基本工资（元）")
    private BigDecimal baseSalary;
    @Schema(description = "岗位工资（元）")
    private BigDecimal positionSalary;
    @Schema(description = "补贴（元）")
    private BigDecimal allowance;
    @Schema(description = "五险（元）")
    private BigDecimal socialInsurance;
    @Schema(description = "公积金（元）")
    private BigDecimal housingFund;
    @Schema(description = "个税（元）")
    private BigDecimal tax;
    @Schema(description = "其他（元）")
    private BigDecimal other;
    @Schema(description = "其他扣款备注说明")
    private String otherRemark;
    @Schema(description = "个人全勤奖金额（元，0=使用全局配置）")
    private BigDecimal fullAttendanceAmount;
    @Schema(description = "生效日期")
    private LocalDate effectiveDate;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
