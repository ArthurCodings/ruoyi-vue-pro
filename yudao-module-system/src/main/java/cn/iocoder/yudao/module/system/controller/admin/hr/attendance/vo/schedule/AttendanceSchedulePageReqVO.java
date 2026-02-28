package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 考勤排班分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceSchedulePageReqVO extends PageParam {

    @Schema(description = "员工用户ID")
    private Long userId;

    @Schema(description = "年月，如 202602")
    private Integer yearMonth;

    @Schema(description = "班次类型：1=早班 2=晚班")
    private Integer shiftType;

}
