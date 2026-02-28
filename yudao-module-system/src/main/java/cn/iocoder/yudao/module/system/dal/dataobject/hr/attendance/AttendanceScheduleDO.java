package cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * HR 月度排班 DO
 */
@TableName("hr_attendance_schedule")
@KeySequence("hr_attendance_schedule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceScheduleDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 员工用户ID */
    private Long userId;
    /** 当日应用的排班规则ID */
    private Long ruleId;
    /** 班次类型快照：1=早班 2=晚班 */
    private Integer shiftType;
    /** 排班日期 */
    private LocalDate scheduleDate;
    /** 年月（如 202602），year 为 MySQL 保留字需反引号转义 */
    @TableField("`year_month`")
    private Integer yearMonth;
    /** 日类型：1=工作日 2=周末休息 3=节假日休息 4=调班工作日 */
    private Integer dayType;
    /** 是否需要打卡 */
    private Boolean isNeedClock;

}
