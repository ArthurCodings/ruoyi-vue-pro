package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "管理后台 - 打卡记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceRecordPageReqVO extends PageParam {

    @Schema(description = "员工用户ID")
    private Long userId;

    @Schema(description = "考勤状态：0=正常 1=迟到 2=早退 3=缺勤")
    private Integer status;

    @Schema(description = "开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @Schema(description = "结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

}
