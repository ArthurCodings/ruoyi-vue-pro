package cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 排班规则分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceRulePageReqVO extends PageParam {

    @Schema(description = "规则名称，模糊匹配")
    private String name;

    @Schema(description = "班次类型：1=早班 2=晚班")
    private Integer shiftType;

    @Schema(description = "状态：0=启用 1=停用")
    private Integer status;

}
