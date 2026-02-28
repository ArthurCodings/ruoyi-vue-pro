package cn.iocoder.yudao.module.system.controller.admin.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceRecordPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceRecordRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRecordDO;
import cn.iocoder.yudao.module.system.service.hr.attendance.AttendanceRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - HR 打卡记录")
@RestController
@RequestMapping("/system/attendance-record")
@Validated
public class AttendanceRecordController {

    @Resource
    private AttendanceRecordService attendanceRecordService;

    @GetMapping("/page")
    @Operation(summary = "获取打卡记录分页列表")
    @PreAuthorize("@ss.hasPermission('system:attendance-schedule:query')")
    public CommonResult<PageResult<AttendanceRecordRespVO>> page(@Validated AttendanceRecordPageReqVO reqVO) {
        PageResult<AttendanceRecordDO> pageResult = attendanceRecordService.getAttendanceRecordPage(reqVO);
        return success(BeanUtils.toBean(pageResult, AttendanceRecordRespVO.class));
    }

    @PostMapping("/clock-in")
    @Operation(summary = "签到打卡（所有在职员工可用）")
    @PreAuthorize("@ss.hasPermission('system:attendance-record:clock')")
    public CommonResult<Long> clockIn(HttpServletRequest request) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String ip = getClientIp(request);
        return success(attendanceRecordService.clockIn(userId, ip));
    }

    @PostMapping("/clock-out")
    @Operation(summary = "签退打卡（所有在职员工可用）")
    @PreAuthorize("@ss.hasPermission('system:attendance-record:clock')")
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
