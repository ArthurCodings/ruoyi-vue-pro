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
    @Schema(description = "员工姓名（用于前端展示）")
    @ExcelProperty("员工姓名")
    private String nickname;
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
    @Schema(description = "五险快照")
    private BigDecimal socialInsurance;
    @Schema(description = "公积金快照")
    private BigDecimal housingFund;
    @Schema(description = "个税快照")
    private BigDecimal tax;
    @Schema(description = "其他快照")
    private BigDecimal other;
    @Schema(description = "其他扣款备注说明")
    private String otherRemark;
    @Schema(description = "全勤奖")
    @ExcelProperty("全勤奖")
    private BigDecimal fullAttendanceBonus;
    @Schema(description = "日薪快照（基本工资÷当月实际工作日数）")
    private BigDecimal dailySalary;
    @Schema(description = "迟到扣款")
    @ExcelProperty("迟到扣款")
    private BigDecimal lateDeduction;
    @Schema(description = "病假天数")
    private Integer sickLeaveDays;
    @Schema(description = "病假扣款（扣20%）")
    @ExcelProperty("病假扣款")
    private BigDecimal sickLeaveDeduction;
    @Schema(description = "事假天数")
    private Integer casualLeaveDays;
    @Schema(description = "事假扣款（扣全天）")
    @ExcelProperty("事假扣款")
    private BigDecimal casualLeaveDeduction;
    @Schema(description = "缺勤扣款")
    @ExcelProperty("缺勤扣款")
    private BigDecimal absentDeduction;
    @Schema(description = "考勤扣款合计")
    @ExcelProperty("考勤扣款")
    private BigDecimal attendanceDeduction;
    @Schema(description = "薪资计算明细JSON（用于前端查看明细弹窗）")
    private String salaryDetail;
    @Schema(description = "关联薪资确认通知单ID")
    private Long confirmNoticeId;
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
