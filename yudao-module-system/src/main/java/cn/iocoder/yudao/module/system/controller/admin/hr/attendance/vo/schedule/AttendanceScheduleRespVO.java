package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 考勤排班 Response VO")
@Data
public class AttendanceScheduleRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "员工用户ID")
    private Long userId;
    @Schema(description = "当日排班规则ID")
    private Long ruleId;
    @Schema(description = "班次类型：1=早班 2=晚班")
    private Integer shiftType;
    @Schema(description = "排班日期")
    private LocalDate scheduleDate;
    @Schema(description = "年月，如 202602")
    private Integer yearMonth;
    @Schema(description = "日类型：1=工作日 2=周末 3=节假日 4=调班工作日")
    private Integer dayType;
    @Schema(description = "是否需要打卡")
    private Boolean isNeedClock;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
