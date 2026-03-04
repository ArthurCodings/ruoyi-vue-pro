package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 打卡记录 Response VO")
@Data
public class AttendanceRecordRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "员工用户ID")
    private Long userId;
    @Schema(description = "员工姓名（用于前端展示）")
    private String nickname;
    @Schema(description = "关联排班ID")
    private Long scheduleId;
    @Schema(description = "考勤日期")
    private LocalDate attendanceDate;
    @Schema(description = "签到时间")
    private LocalDateTime clockInTime;
    @Schema(description = "签退时间")
    private LocalDateTime clockOutTime;
    @Schema(description = "签到IP")
    private String clockInIp;
    @Schema(description = "签退IP")
    private String clockOutIp;
    @Schema(description = "考勤状态：0=正常 1=迟到 2=早退 3=缺勤 4=请假 5=加班")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
