package cn.iocoder.yudao.module.system.dal.dataobject.hr.salary;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * HR 员工薪资档案 DO
 */
@TableName("hr_employee_salary")
@KeySequence("hr_employee_salary_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeSalaryDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 员工用户ID */
    private Long userId;
    /** 基本工资（元） */
    private BigDecimal baseSalary;
    /** 岗位工资（元） */
    private BigDecimal positionSalary;
    /** 补贴（元） */
    private BigDecimal allowance;
    /** 五险（元） */
    private BigDecimal socialInsurance;
    /** 公积金（元） */
    private BigDecimal housingFund;
    /** 个税（元） */
    private BigDecimal tax;
    /** 其他（元，杂项扣款） */
    private BigDecimal other;
    /** 其他扣款备注说明 */
    private String otherRemark;
    /** 个人全勤奖金额（元，0=使用全局配置 hr_salary_config.full_attendance_amount） */
    private BigDecimal fullAttendanceAmount;
    /** 生效日期 */
    private LocalDate effectiveDate;
    /** 备注（如调薪原因） */
    private String remark;

}
