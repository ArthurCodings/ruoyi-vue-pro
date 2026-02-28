package cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 员工薪资档案分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeSalaryPageReqVO extends PageParam {

    @Schema(description = "员工用户ID")
    private Long userId;

}
