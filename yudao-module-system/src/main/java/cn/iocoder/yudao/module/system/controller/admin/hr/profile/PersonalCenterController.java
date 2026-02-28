package cn.iocoder.yudao.module.system.controller.admin.hr.profile;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.profile.vo.PersonalCenterPanelVO;
import cn.iocoder.yudao.module.system.service.hr.attendance.AttendanceRecordService;
import cn.iocoder.yudao.module.system.service.hr.profile.PersonalCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 个人中心聚合面板")
@RestController
@RequestMapping("/system/personal-center")
@Validated
public class PersonalCenterController {

    @Resource
    private PersonalCenterService personalCenterService;
    @Resource
    private AttendanceRecordService attendanceRecordService;

    @GetMapping("/panel")
    @Operation(summary = "获取个人中心聚合面板（员工档案 + 本月考勤 + 最新薪资 + 我的合同）",
               description = "登录即可访问，无需额外权限。yearMonth 不传则默认当月。")
    @Parameter(name = "yearMonth", description = "目标年月，如 202603，不传则当月", example = "202603")
    public CommonResult<PersonalCenterPanelVO> getPanel(
            @RequestParam(value = "yearMonth", required = false) Integer yearMonth) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(personalCenterService.getPanel(userId, yearMonth));
    }

    @PostMapping("/clock-in")
    @Operation(summary = "快捷签到（首页打卡入口）",
               description = "与 /system/attendance-record/clock-in 功能一致，放在此处方便首页直接调用。")
    public CommonResult<Long> clockIn(HttpServletRequest request) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String ip = getClientIp(request);
        return success(attendanceRecordService.clockIn(userId, ip));
    }

    @PostMapping("/clock-out")
    @Operation(summary = "快捷签退（首页打卡入口）")
    public CommonResult<Boolean> clockOut(HttpServletRequest request) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String ip = getClientIp(request);
        attendanceRecordService.clockOut(userId, ip);
        return success(true);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
