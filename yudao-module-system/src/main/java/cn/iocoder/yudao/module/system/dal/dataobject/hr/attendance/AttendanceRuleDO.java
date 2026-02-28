package cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * HR 排班规则 DO
 */
@TableName("hr_attendance_rule")
@KeySequence("hr_attendance_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceRuleDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 规则名称（如：早班、晚班） */
    private String name;
    /** 班次类型：1=早班 2=晚班 */
    private Integer shiftType;
    /** 每周工作天数：5=周一至周五，6=含周六 */
    private Integer workDaysPerWeek;
    /** 上班时间 */
    private LocalTime workStartTime;
    /** 下班时间 */
    private LocalTime workEndTime;
    /** 迟到阈值（分钟） */
    private Integer lateThresholdMinutes;
    /** 早退阈值（分钟） */
    private Integer earlyLeaveThresholdMinutes;
    /** 是否遵循法定节假日 */
    private Boolean isFollowHoliday;
    /** 状态：0=启用 1=停用 */
    private Integer status;
    /** 备注 */
    private String remark;

}
