package cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 月度薪资明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SalaryMonthlyRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "员工用户ID")
    @ExcelProperty("员工ID")
    private Long userId;
    @Schema(description = "年月，如 202602")
    @ExcelProperty("年月")
    private Integer yearMonth;
    @Schema(description = "基本工资快照")
    @ExcelProperty("基本工资")
    private BigDecimal baseSalary;
    @Schema(description = "岗位工资快照")
    @ExcelProperty("岗位工资")
    private BigDecimal positionSalary;
    @Schema(description = "绩效")
    @ExcelProperty("绩效")
    private BigDecimal performance;
    @Schema(description = "提成")
    @ExcelProperty("提成")
    private BigDecimal commission;
    @Schema(description = "补贴快照")
    @ExcelProperty("补贴")
    private BigDecimal allowance;
    @Schema(description = "全勤奖")
    @ExcelProperty("全勤奖")
    private BigDecimal fullAttendanceBonus;
    @Schema(description = "应发工资合计")
    @ExcelProperty("应发工资")
    private BigDecimal totalSalary;
    @Schema(description = "应出勤天数")
    private Integer shouldAttendDays;
    @Schema(description = "实际出勤天数")
    private Integer actualAttendDays;
    @Schema(description = "迟到次数")
    private Integer lateCount;
    @Schema(description = "缺勤天数")
    private Integer absentCount;
    @Schema(description = "状态：0=待确认 1=已确认 2=已发放")
    @ExcelProperty("状态")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
