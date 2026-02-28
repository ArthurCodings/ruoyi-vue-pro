package cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 月度薪资分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SalaryMonthlyPageReqVO extends PageParam {

    @Schema(description = "员工用户ID")
    private Long userId;

    @Schema(description = "年月，如 202602")
    private Integer yearMonth;

    @Schema(description = "状态：0=待确认 1=已确认 2=已发放")
    private Integer status;

}
