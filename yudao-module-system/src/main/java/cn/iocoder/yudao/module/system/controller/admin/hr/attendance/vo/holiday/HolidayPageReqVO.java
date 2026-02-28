package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 法定节假日分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class HolidayPageReqVO extends PageParam {

    @Schema(description = "年份")
    private Integer year;

    @Schema(description = "类型：1=休息日 2=调班工作日")
    private Integer type;

    @Schema(description = "节假日名称，模糊匹配")
    private String name;

}
