package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 法定节假日 Response VO")
@Data
public class HolidayRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "节假日名称")
    private String name;
    @Schema(description = "具体日期")
    private LocalDate holidayDate;
    @Schema(description = "类型：1=休息日 2=调班工作日")
    private Integer type;
    @Schema(description = "所属年份")
    private Integer year;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
