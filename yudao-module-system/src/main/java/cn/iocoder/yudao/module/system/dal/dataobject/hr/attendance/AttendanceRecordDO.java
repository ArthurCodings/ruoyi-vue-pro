package cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HR 打卡记录 DO
 */
@TableName("hr_attendance_record")
@KeySequence("hr_attendance_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceRecordDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 员工用户ID */
    private Long userId;
    /** 关联排班ID（0=无对应排班的异常打卡） */
    private Long scheduleId;
    /** 考勤日期（冗余） */
    private LocalDate attendanceDate;
    /** 签到时间 */
    private LocalDateTime clockInTime;
    /** 签退时间 */
    private LocalDateTime clockOutTime;
    /** 签到IP */
    private String clockInIp;
    /** 签退IP */
    private String clockOutIp;
    /** 考勤状态：0=正常 1=迟到 2=早退 3=缺勤 4=请假 5=加班 */
    private Integer status;
    /** 备注 */
    private String remark;

}
