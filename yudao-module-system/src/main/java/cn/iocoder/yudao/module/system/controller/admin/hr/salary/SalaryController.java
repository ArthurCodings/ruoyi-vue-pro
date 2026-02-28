package cn.iocoder.yudao.module.system.controller.admin.hr.salary;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalaryPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalaryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalarySaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.EmployeeSalaryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryMonthlyDO;
import cn.iocoder.yudao.module.system.service.hr.salary.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - HR 薪资管理")
@RestController
@RequestMapping("/system/salary")
@Validated
public class SalaryController {

    @Resource
    private SalaryService salaryService;

    // ===== 薪资配置 =====

    @GetMapping("/config/get")
    @Operation(summary = "获取薪资配置")
    @PreAuthorize("@ss.hasPermission('system:salary-config:query')")
    public CommonResult<SalaryConfigRespVO> getConfig() {
        return success(salaryService.getSalaryConfig());
    }

    @PostMapping("/config/save")
    @Operation(summary = "保存薪资配置")
    @PreAuthorize("@ss.hasPermission('system:salary-config:update')")
    public CommonResult<Boolean> saveConfig(@Valid @RequestBody SalaryConfigSaveReqVO reqVO) {
        salaryService.saveSalaryConfig(reqVO);
        return success(true);
    }

    // ===== 员工薪资档案 =====

    @PostMapping("/employee/create")
    @Operation(summary = "创建员工薪资档案")
    @PreAuthorize("@ss.hasPermission('system:employee-salary:update')")
    public CommonResult<Long> createEmployeeSalary(@Valid @RequestBody EmployeeSalarySaveReqVO reqVO) {
        return success(salaryService.createEmployeeSalary(reqVO));
    }

    @PutMapping("/employee/update")
    @Operation(summary = "修改员工薪资档案")
    @PreAuthorize("@ss.hasPermission('system:employee-salary:update')")
    public CommonResult<Boolean> updateEmployeeSalary(@Valid @RequestBody EmployeeSalarySaveReqVO reqVO) {
        salaryService.updateEmployeeSalary(reqVO);
        return success(true);
    }

    @GetMapping("/employee/page")
    @Operation(summary = "获取员工薪资档案分页列表")
    @PreAuthorize("@ss.hasPermission('system:employee-salary:query')")
    public CommonResult<PageResult<EmployeeSalaryRespVO>> employeePage(@Validated EmployeeSalaryPageReqVO reqVO) {
        PageResult<EmployeeSalaryDO> pageResult = salaryService.getEmployeeSalaryPage(reqVO);
        return success(BeanUtils.toBean(pageResult, EmployeeSalaryRespVO.class));
    }

    // ===== 月度薪资 =====

    @GetMapping("/monthly/page")
    @Operation(summary = "获取月度薪资分页列表")
    @PreAuthorize("@ss.hasPermission('system:salary-monthly:query')")
    public CommonResult<PageResult<SalaryMonthlyRespVO>> monthlyPage(@Validated SalaryMonthlyPageReqVO reqVO) {
        PageResult<SalaryMonthlyDO> pageResult = salaryService.getSalaryMonthlyPage(reqVO);
        return success(BeanUtils.toBean(pageResult, SalaryMonthlyRespVO.class));
    }

    @PostMapping("/monthly/generate")
    @Operation(summary = "发起月结算（生成草稿）")
    @Parameter(name = "yearMonth", description = "年月，如 202603", required = true)
    @PreAuthorize("@ss.hasPermission('system:salary-monthly:update')")
    public CommonResult<Boolean> generate(@RequestParam("yearMonth") Integer yearMonth) {
        salaryService.generateMonthly(yearMonth);
        return success(true);
    }

    @PutMapping("/monthly/update")
    @Operation(summary = "编辑薪资单（填写绩效/提成）")
    @PreAuthorize("@ss.hasPermission('system:salary-monthly:update')")
    public CommonResult<Boolean> updateMonthly(@Valid @RequestBody SalaryMonthlyUpdateReqVO reqVO) {
        salaryService.updateSalaryMonthly(reqVO);
        return success(true);
    }

    @PutMapping("/monthly/confirm")
    @Operation(summary = "确认薪资单")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('system:salary-monthly:update')")
    public CommonResult<Boolean> confirm(@RequestParam("id") Long id) {
        salaryService.confirmSalaryMonthly(id);
        return success(true);
    }

    @PutMapping("/monthly/batch-confirm")
    @Operation(summary = "批量确认薪资单")
    @PreAuthorize("@ss.hasPermission('system:salary-monthly:update')")
    public CommonResult<Boolean> batchConfirm(@RequestParam("ids") List<Long> ids) {
        salaryService.batchConfirmSalaryMonthly(ids);
        return success(true);
    }

    @GetMapping("/monthly/export-excel")
    @Operation(summary = "导出月度薪资明细")
    @PreAuthorize("@ss.hasPermission('system:salary-monthly:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExcel(HttpServletResponse response, @Validated SalaryMonthlyPageReqVO reqVO) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SalaryMonthlyDO> list = salaryService.getSalaryMonthlyPage(reqVO).getList();
        ExcelUtils.write(response, "月度薪资.xls", "薪资明细", SalaryMonthlyRespVO.class,
                BeanUtils.toBean(list, SalaryMonthlyRespVO.class));
    }

    // ===== 个人中心 =====

    @GetMapping("/my-latest")
    @Operation(summary = "个人中心 - 最新薪资单")
    public CommonResult<SalaryMonthlyRespVO> myLatest() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(salaryService.getMyLatestSalary(userId));
    }

    @GetMapping("/my-list")
    @Operation(summary = "个人中心 - 历史薪资列表")
    public CommonResult<List<SalaryMonthlyRespVO>> myList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(salaryService.getMySalaryList(userId));
    }

}
