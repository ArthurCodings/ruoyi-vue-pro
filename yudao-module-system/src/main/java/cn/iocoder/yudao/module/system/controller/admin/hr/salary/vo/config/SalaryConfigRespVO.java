package cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 薪资配置 Response VO")
@Data
public class SalaryConfigRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "是否启用基本工资")
    private Boolean enableBaseSalary;
    @Schema(description = "是否启用岗位工资")
    private Boolean enablePositionSalary;
    @Schema(description = "是否启用绩效")
    private Boolean enablePerformance;
    @Schema(description = "是否启用提成")
    private Boolean enableCommission;
    @Schema(description = "是否启用补贴")
    private Boolean enableAllowance;
    @Schema(description = "是否启用全勤奖")
    private Boolean enableFullAttendance;
    @Schema(description = "全勤奖金额（元）")
    private BigDecimal fullAttendanceAmount;
    @Schema(description = "提成来源：1=合同管理 2=手动录入")
    private Integer commissionSource;
    @Schema(description = "绩效来源：1=绩效审核表 2=手动录入")
    private Integer performanceSource;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
