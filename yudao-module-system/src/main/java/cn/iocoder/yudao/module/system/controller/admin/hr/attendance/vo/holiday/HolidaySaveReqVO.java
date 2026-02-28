package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 法定节假日创建/修改 Request VO")
@Data
public class HolidaySaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "节假日名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "节假日名称不能为空")
    private String name;

    @Schema(description = "具体日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "日期不能为空")
    private LocalDate holidayDate;

    @Schema(description = "类型：1=休息日 2=调班工作日", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "所属年份", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "年份不能为空")
    private Integer year;

    @Schema(description = "备注")
    private String remark;

}
