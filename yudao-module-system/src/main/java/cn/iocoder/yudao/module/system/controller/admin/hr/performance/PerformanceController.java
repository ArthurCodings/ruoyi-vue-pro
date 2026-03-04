package cn.iocoder.yudao.module.system.controller.admin.hr.performance;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo.*;
import cn.iocoder.yudao.module.system.service.hr.employee.HrEmployeeService;
import cn.iocoder.yudao.module.system.service.hr.performance.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - HR 绩效管理")
@RestController
@RequestMapping("/system/performance")
@Validated
public class PerformanceController {

    @Resource
    private PerformanceService performanceService;
    @Resource
    private HrEmployeeService hrEmployeeService;

    // ===== 绩效模板管理 =====

    @PostMapping("/template/create")
    @Operation(summary = "创建绩效评分模板")
    @PreAuthorize("@ss.hasPermission('system:performance-template:update')")
    public CommonResult<Long> createTemplate(@Valid @RequestBody PerformanceTemplateSaveReqVO reqVO) {
        return success(performanceService.createTemplate(reqVO));
    }

    @PutMapping("/template/update")
    @Operation(summary = "修改绩效评分模板")
    @PreAuthorize("@ss.hasPermission('system:performance-template:update')")
    public CommonResult<Boolean> updateTemplate(@Valid @RequestBody PerformanceTemplateSaveReqVO reqVO) {
        performanceService.updateTemplate(reqVO);
        return success(true);
    }

    @DeleteMapping("/template/delete")
    @Operation(summary = "删除绩效评分模板")
    @Parameter(name = "id", description = "模板ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:performance-template:update')")
    public CommonResult<Boolean> deleteTemplate(@RequestParam("id") Long id) {
        performanceService.deleteTemplate(id);
        return success(true);
    }

    @GetMapping("/template/get")
    @Operation(summary = "获取绩效评分模板详情")
    @Parameter(name = "id", description = "模板ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:performance-template:query')")
    public CommonResult<PerformanceTemplateRespVO> getTemplate(@RequestParam("id") Long id) {
        return success(performanceService.getTemplate(id));
    }

    @GetMapping("/template/list")
    @Operation(summary = "获取所有启用的绩效模板列表")
    @PreAuthorize("@ss.hasPermission('system:performance-template:query')")
    public CommonResult<List<PerformanceTemplateRespVO>> templateList() {
        return success(performanceService.getTemplateList());
    }

    @PutMapping("/template/set-users")
    @Operation(summary = "设置模板应用人员")
    @Parameter(name = "templateId", description = "模板ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:performance-template:update')")
    public CommonResult<Boolean> setTemplateUsers(@RequestParam("templateId") Long templateId,
                                                   @RequestBody List<Long> userIds) {
        performanceService.setTemplateUsers(templateId, userIds);
        return success(true);
    }

    // ===== 绩效打分 =====

    @PostMapping("/score/save")
    @Operation(summary = "创建/修改绩效打分记录（HR或考核人操作）")
    @PreAuthorize("@ss.hasPermission('system:performance-score:update')")
    public CommonResult<Long> saveScore(@Valid @RequestBody PerformanceScoreSaveReqVO reqVO) {
        return success(performanceService.saveScore(reqVO));
    }

    @PutMapping("/score/submit")
    @Operation(summary = "提交绩效打分（草稿→待审核）")
    @Parameter(name = "id", description = "打分记录ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:performance-score:update')")
    public CommonResult<Boolean> submitScore(@RequestParam("id") Long id) {
        performanceService.submitScore(id);
        return success(true);
    }

    @PutMapping("/score/approve")
    @Operation(summary = "审核通过绩效打分")
    @Parameter(name = "id", description = "打分记录ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:performance-score:review')")
    public CommonResult<Boolean> approveScore(@RequestParam("id") Long id,
                                               @RequestParam(value = "reviewComment", required = false) String reviewComment) {
        Long reviewerUserId = SecurityFrameworkUtils.getLoginUserId();
        performanceService.approveScore(id, reviewComment, reviewerUserId);
        return success(true);
    }

    @PutMapping("/score/reject")
    @Operation(summary = "驳回绩效打分")
    @Parameter(name = "id", description = "打分记录ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:performance-score:review')")
    public CommonResult<Boolean> rejectScore(@RequestParam("id") Long id,
                                              @RequestParam(value = "reviewComment", required = false) String reviewComment) {
        performanceService.rejectScore(id, reviewComment);
        return success(true);
    }

    @GetMapping("/score/get")
    @Operation(summary = "获取绩效打分详情")
    @Parameter(name = "id", description = "打分记录ID", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('system:performance-score:query', 'system:performance-score:update')")
    public CommonResult<PerformanceScoreRespVO> getScore(@RequestParam("id") Long id) {
        PerformanceScoreRespVO vo = performanceService.getScore(id);
        fillNickname(vo);
        return success(vo);
    }

    @GetMapping("/score/page")
    @Operation(summary = "分页查询绩效打分列表")
    @PreAuthorize("@ss.hasPermission('system:performance-score:query')")
    public CommonResult<PageResult<PerformanceScoreRespVO>> scorePage(
            @RequestParam(value = "yearMonth", required = false) Integer yearMonth,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) Integer status,
            @Validated PageParam pageParam) {
        PageResult<PerformanceScoreRespVO> result = performanceService.getScorePage(yearMonth, userId, status, pageParam);
        fillNicknameList(result.getList());
        return success(result);
    }

    // ===== 个人中心 =====

    @GetMapping("/my-score")
    @Operation(summary = "个人中心 - 查询本人某月的绩效打分")
    @Parameter(name = "yearMonth", description = "年月，如202603", required = true)
    public CommonResult<PerformanceScoreRespVO> myScore(@RequestParam("yearMonth") Integer yearMonth) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(performanceService.getMyScore(userId, yearMonth));
    }

    @PutMapping("/my-appeal")
    @Operation(summary = "个人中心 - 员工提交绩效申诉")
    @Parameter(name = "id", description = "打分记录ID", required = true)
    public CommonResult<Boolean> myAppeal(@RequestParam("id") Long id,
                                           @RequestParam("appealContent") String appealContent) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        performanceService.appealScore(id, userId, appealContent);
        return success(true);
    }

    private void fillNickname(PerformanceScoreRespVO vo) {
        if (vo == null || vo.getUserId() == null) return;
        Map<Long, String> map = hrEmployeeService.getNicknameMap(Set.of(vo.getUserId()));
        vo.setNickname(map.getOrDefault(vo.getUserId(), ""));
    }

    private void fillNicknameList(List<PerformanceScoreRespVO> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> userIds = list.stream().map(PerformanceScoreRespVO::getUserId).collect(Collectors.toSet());
        Map<Long, String> map = hrEmployeeService.getNicknameMap(userIds);
        list.forEach(vo -> vo.setNickname(map.getOrDefault(vo.getUserId(), "")));
    }

}
