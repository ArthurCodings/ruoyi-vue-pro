package cn.iocoder.yudao.module.system.controller.admin.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceSchedulePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceScheduleRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceScheduleUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceScheduleDO;
import cn.iocoder.yudao.module.system.service.hr.attendance.AttendanceScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - HR 考勤排班")
@RestController
@RequestMapping("/system/attendance-schedule")
@Validated
public class AttendanceScheduleController {

    @Resource
    private AttendanceScheduleService attendanceScheduleService;

    @GetMapping("/page")
    @Operation(summary = "获取排班分页列表")
    @PreAuthorize("@ss.hasPermission('system:attendance-schedule:query')")
    public CommonResult<PageResult<AttendanceScheduleRespVO>> page(@Validated AttendanceSchedulePageReqVO reqVO) {
        PageResult<AttendanceScheduleDO> pageResult = attendanceScheduleService.getAttendanceSchedulePage(reqVO);
        return success(BeanUtils.toBean(pageResult, AttendanceScheduleRespVO.class));
    }

    @PostMapping("/generate")
    @Operation(summary = "手动触发生成排班")
    @Parameter(name = "yearMonth", description = "目标年月，如 202603。为空则生成下个月")
    @PreAuthorize("@ss.hasPermission('system:attendance-schedule:update')")
    public CommonResult<Boolean> generate(@RequestParam(value = "yearMonth", required = false) Integer yearMonth) {
        attendanceScheduleService.generateMonthlySchedule(yearMonth);
        return success(true);
    }

    @PutMapping("/change-shift")
    @Operation(summary = "临时调班（修改某人某天的班次规则）")
    @PreAuthorize("@ss.hasPermission('system:attendance-schedule:update')")
    public CommonResult<Boolean> changeShift(@Valid @RequestBody AttendanceScheduleUpdateReqVO reqVO) {
        attendanceScheduleService.updateScheduleRule(reqVO);
        return success(true);
    }

    @Resource
    private cn.iocoder.yudao.module.system.service.hr.attendance.AttendanceRecordService attendanceRecordService;

    @GetMapping("/my-monthly-summary")
    @Operation(summary = "个人中心 - 本月考勤汇总")
    public CommonResult<cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceMonthlySummaryVO> myMonthlySummary(
            @RequestParam(value = "yearMonth", required = false) Integer yearMonth) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (yearMonth == null) {
            YearMonth ym = YearMonth.now();
            yearMonth = ym.getYear() * 100 + ym.getMonthValue();
        }
        return success(attendanceRecordService.getMyMonthlySummary(userId, yearMonth));
    }

    @GetMapping("/my-monthly")
    @Operation(summary = "个人中心 - 本月每日排班列表")
    public CommonResult<List<AttendanceScheduleRespVO>> myMonthly(
            @RequestParam(value = "yearMonth", required = false) Integer yearMonth) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (yearMonth == null) {
            YearMonth ym = YearMonth.now();
            yearMonth = ym.getYear() * 100 + ym.getMonthValue();
        }
        return success(attendanceScheduleService.getMyMonthlySchedule(userId, yearMonth));
    }

}
