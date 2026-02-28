package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Schema(description = "管理后台 - 排班规则创建/修改 Request VO")
@Data
public class AttendanceRuleSaveReqVO {

    @Schema(description = "规则ID")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "规则名称不能为空")
    private String name;

    @Schema(description = "班次类型：1=早班 2=晚班", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "班次类型不能为空")
    private Integer shiftType;

    @Schema(description = "每周工作天数：5=周一至周五，6=含周六", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "每周工作天数不能为空")
    private Integer workDaysPerWeek;

    @Schema(description = "上班时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "上班时间不能为空")
    private LocalTime workStartTime;

    @Schema(description = "下班时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "下班时间不能为空")
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

}
