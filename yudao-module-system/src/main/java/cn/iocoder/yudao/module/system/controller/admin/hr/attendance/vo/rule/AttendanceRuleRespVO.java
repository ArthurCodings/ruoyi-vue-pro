package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "管理后台 - 排班规则 Response VO")
@Data
public class AttendanceRuleRespVO {

    @Schema(description = "规则ID")
    private Long id;
    @Schema(description = "规则名称")
    private String name;
    @Schema(description = "班次类型：1=早班 2=晚班")
    private Integer shiftType;
    @Schema(description = "每周工作天数")
    private Integer workDaysPerWeek;
    @Schema(description = "上班时间")
    private LocalTime workStartTime;
    @Schema(description = "下班时间")
    private LocalTime workEndTime;
    @Schema(description = "迟到阈值（分钟）")
    private Integer lateThresholdMinutes;
    @Schema(description = "早退阈值（分钟）")
    private Integer earlyLeaveThresholdMinutes;
    @Schema(description = "是否遵循法定节假日")
    private Boolean isFollowHoliday;
    @Schema(description = "状态：0=启用 1=停用")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
