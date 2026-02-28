package cn.iocoder.yudao.module.system.dal.dataobject.hr.salary;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * HR 月度薪资明细 DO
 */
@TableName("hr_salary_monthly")
@KeySequence("hr_salary_monthly_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SalaryMonthlyDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 员工用户ID */
    private Long userId;
    /** 年月（如 202602），year 为 MySQL 保留字需反引号转义 */
    @TableField("`year_month`")
    private Integer yearMonth;
    /** 基本工资快照 */
    private BigDecimal baseSalary;
    /** 岗位工资快照 */
    private BigDecimal positionSalary;
    /** 绩效 */
    private BigDecimal performance;
    /** 提成 */
    private BigDecimal commission;
    /** 补贴快照 */
    private BigDecimal allowance;
    /** 全勤奖 */
    private BigDecimal fullAttendanceBonus;
    /** 应发工资合计 */
    private BigDecimal totalSalary;
    /** 应出勤天数 */
    private Integer shouldAttendDays;
    /** 实际出勤天数 */
    private Integer actualAttendDays;
    /** 迟到次数 */
    private Integer lateCount;
    /** 缺勤天数 */
    private Integer absentCount;
    /** 状态：0=待确认 1=已确认 2=已发放 */
    private Integer status;
    /** 备注 */
    private String remark;

}
