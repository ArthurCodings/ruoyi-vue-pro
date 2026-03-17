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
    /** 五险快照 */
    private BigDecimal socialInsurance;
    /** 公积金快照 */
    private BigDecimal housingFund;
    /** 个税快照 */
    private BigDecimal tax;
    /** 其他（杂项）快照 */
    private BigDecimal other;
    /** 其他扣款备注说明快照 */
    private String otherRemark;
    /** 全勤奖 */
    private BigDecimal fullAttendanceBonus;
    /** 日薪快照（基本工资 ÷ 当月实际工作日数，四舍五入保留2位） */
    private BigDecimal dailySalary;
    /** 迟到扣款（第2次起每次扣30元） */
    private BigDecimal lateDeduction;
    /** 病假天数 */
    private Integer sickLeaveDays;
    /** 病假扣款（扣20%：日薪 × 病假天数 × 0.2） */
    private BigDecimal sickLeaveDeduction;
    /** 事假天数 */
    private Integer casualLeaveDays;
    /** 事假扣款（扣全天：日薪 × 事假天数） */
    private BigDecimal casualLeaveDeduction;
    /** 缺勤扣款（扣全天：日薪 × 缺勤天数） */
    private BigDecimal absentDeduction;
    /** 考勤扣款合计 = 迟到扣款 + 病假扣款 + 事假扣款 + 缺勤扣款 */
    private BigDecimal attendanceDeduction;
    /** 薪资计算明细JSON（供前端"查看明细"使用） */
    private String salaryDetail;
    /** 关联薪资确认通知单ID（0=尚未发送通知） */
    private Long confirmNoticeId;
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
