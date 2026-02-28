package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 考勤排班调班 Request VO")
@Data
public class AttendanceScheduleUpdateReqVO {

    @Schema(description = "排班ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排班ID不能为空")
    private Long id;

    @Schema(description = "调整为的规则ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规则ID不能为空")
    private Long ruleId;

}
