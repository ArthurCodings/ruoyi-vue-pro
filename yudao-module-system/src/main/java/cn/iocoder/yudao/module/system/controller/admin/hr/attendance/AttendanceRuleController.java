package cn.iocoder.yudao.module.system.controller.admin.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule.AttendanceRulePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule.AttendanceRuleRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule.AttendanceRuleSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRuleDO;
import cn.iocoder.yudao.module.system.service.hr.attendance.AttendanceRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - HR 排班规则")
@RestController
@RequestMapping("/system/attendance-rule")
@Validated
public class AttendanceRuleController {

    @Resource
    private AttendanceRuleService attendanceRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建排班规则")
    @PreAuthorize("@ss.hasPermission('system:attendance-rule:update')")
    public CommonResult<Long> create(@Valid @RequestBody AttendanceRuleSaveReqVO reqVO) {
        return success(attendanceRuleService.createAttendanceRule(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改排班规则")
    @PreAuthorize("@ss.hasPermission('system:attendance-rule:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody AttendanceRuleSaveReqVO reqVO) {
        attendanceRuleService.updateAttendanceRule(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排班规则")
    @Parameter(name = "id", description = "规则ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:attendance-rule:update')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        attendanceRuleService.deleteAttendanceRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取排班规则")
    @Parameter(name = "id", description = "规则ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:attendance-rule:query')")
    public CommonResult<AttendanceRuleRespVO> get(@RequestParam("id") Long id) {
        AttendanceRuleDO rule = attendanceRuleService.getAttendanceRule(id);
        return success(BeanUtils.toBean(rule, AttendanceRuleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取排班规则分页列表")
    @PreAuthorize("@ss.hasPermission('system:attendance-rule:query')")
    public CommonResult<PageResult<AttendanceRuleRespVO>> page(@Validated AttendanceRulePageReqVO reqVO) {
        PageResult<AttendanceRuleDO> pageResult = attendanceRuleService.getAttendanceRulePage(reqVO);
        return success(BeanUtils.toBean(pageResult, AttendanceRuleRespVO.class));
    }

    @GetMapping("/list-enabled")
    @Operation(summary = "获取所有启用的排班规则（用于下拉选项）")
    @PreAuthorize("@ss.hasPermission('system:attendance-rule:query')")
    public CommonResult<List<AttendanceRuleRespVO>> listEnabled() {
        List<AttendanceRuleDO> list = attendanceRuleService.getEnabledRuleList();
        return success(BeanUtils.toBean(list, AttendanceRuleRespVO.class));
    }

}
