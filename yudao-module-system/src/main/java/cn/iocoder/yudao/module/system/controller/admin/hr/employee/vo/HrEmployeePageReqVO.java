package cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 员工花名册分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class HrEmployeePageReqVO extends PageParam {

    @Schema(description = "姓名，模糊匹配")
    private String nickname;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "雇佣类型：1=全职 2=实习 3=兼职")
    private Integer employmentType;

    @Schema(description = "在职状态：1=在职 2=离职 3=待入职")
    private Integer employmentStatus;

}
