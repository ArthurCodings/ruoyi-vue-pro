package cn.iocoder.yudao.module.system.dal.dataobject.hr.salary;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * HR 薪资项目配置 DO（全公司仅一条记录）
 */
@TableName("hr_salary_config")
@KeySequence("hr_salary_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SalaryConfigDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 是否启用基本工资 */
    private Boolean enableBaseSalary;
    /** 是否启用岗位工资 */
    private Boolean enablePositionSalary;
    /** 是否启用绩效 */
    private Boolean enablePerformance;
    /** 是否启用提成 */
    private Boolean enableCommission;
    /** 是否启用补贴 */
    private Boolean enableAllowance;
    /** 是否启用全勤奖 */
    private Boolean enableFullAttendance;
    /** 全勤奖金额（元） */
    private BigDecimal fullAttendanceAmount;
    /** 提成来源：1=合同管理 2=手动录入 */
    private Integer commissionSource;
    /** 绩效来源：1=绩效审核表 2=手动录入 */
    private Integer performanceSource;
    /** 备注 */
    private String remark;

}
