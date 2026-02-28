package cn.iocoder.yudao.module.system.controller.admin.hr.profile.vo;

import cn.iocoder.yudao.module.infra.api.contract.dto.ContractSimpleDTO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceMonthlySummaryVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 个人中心聚合面板 VO
 * 前端一次请求获取所有个人相关数据
 */
@Schema(description = "个人中心 - 聚合面板 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalCenterPanelVO {

    @Schema(description = "员工档案（花名册信息）")
    private HrEmployeeRespVO employee;

    @Schema(description = "本月考勤汇总（应出勤/实出勤/迟到/缺勤/每日状态）")
    private AttendanceMonthlySummaryVO attendance;

    @Schema(description = "最新薪资单（已确认/已发放状态，草稿不可见）")
    private SalaryMonthlyRespVO latestSalary;

    @Schema(description = "薪资公式配置（让员工了解计算规则）")
    private SalaryConfigRespVO salaryConfig;

    @Schema(description = "我负责的合同列表")
    private List<ContractSimpleDTO> contracts;

    @Schema(description = "当前年月，格式 202603，用于前端刷新判断")
    private Integer currentYearMonth;

    @Schema(description = "是否已完成今日签到")
    private Boolean todayClockIn;

    @Schema(description = "是否已完成今日签退")
    private Boolean todayClockOut;

}
