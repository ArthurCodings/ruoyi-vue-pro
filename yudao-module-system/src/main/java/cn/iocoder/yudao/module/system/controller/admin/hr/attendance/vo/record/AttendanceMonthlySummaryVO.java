package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "个人中心 - 本月考勤汇总 VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceMonthlySummaryVO {

    @Schema(description = "应出勤天数")
    private Integer shouldAttendDays;
    @Schema(description = "实际出勤天数")
    private Integer actualAttendDays;
    @Schema(description = "迟到次数")
    private Integer lateCount;
    @Schema(description = "缺勤天数")
    private Integer absentCount;
    @Schema(description = "每日打卡状态列表（用于日历渲染）")
    private List<DailyStatusVO> dailyList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStatusVO {
        @Schema(description = "日期")
        private LocalDate date;
        @Schema(description = "考勤状态（-1=休息/不需打卡 0=正常 1=迟到 2=早退 3=缺勤）")
        private Integer status;
    }

}
