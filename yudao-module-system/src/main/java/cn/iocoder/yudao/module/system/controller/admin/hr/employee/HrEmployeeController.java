package cn.iocoder.yudao.module.system.controller.admin.hr.employee;

import cn.hutool.core.util.DesensitizedUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkService;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;
import cn.iocoder.yudao.module.system.service.hr.employee.HrEmployeeService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
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

@Tag(name = "管理后台 - HR 员工花名册")
@RestController
@RequestMapping("/system/hr-employee")
@Validated
public class HrEmployeeController {

    @Resource
    private HrEmployeeService hrEmployeeService;
    @Resource
    private SecurityFrameworkService securityFrameworkService;
    @Resource
    private AdminUserService adminUserService;

    @PostMapping("/create")
    @Operation(summary = "创建员工花名册")
    @PreAuthorize("@ss.hasPermission('system:hr-employee:update')")
    public CommonResult<Long> create(@Valid @RequestBody HrEmployeeSaveReqVO reqVO) {
        return success(hrEmployeeService.createEmployee(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改员工花名册")
    @PreAuthorize("@ss.hasPermission('system:hr-employee:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody HrEmployeeSaveReqVO reqVO) {
        hrEmployeeService.updateEmployee(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除员工花名册记录")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('system:hr-employee:update')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        hrEmployeeService.deleteEmployee(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取员工花名册详情")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('system:hr-employee:query')")
    public CommonResult<HrEmployeeRespVO> get(@RequestParam("id") Long id) {
        HrEmployeeDO emp = hrEmployeeService.getEmployee(id);
        HrEmployeeRespVO vo = BeanUtils.toBean(emp, HrEmployeeRespVO.class);
        // 填充关联用户账号，用于前端展示「姓名 (username)」
        fillUsername(vo);
        // 无敏感权限时脱敏
        if (!securityFrameworkService.hasPermission("system:hr-employee:sensitive")) {
            if (vo.getIdCard() != null && !vo.getIdCard().isEmpty()) {
                vo.setIdCard(DesensitizedUtil.idCardNum(vo.getIdCard(), 4, 4));
            }
        }
        return success(vo);
    }

    @GetMapping("/page")
    @Operation(summary = "获取员工花名册分页列表")
    @PreAuthorize("@ss.hasPermission('system:hr-employee:query')")
    public CommonResult<PageResult<HrEmployeeRespVO>> page(@Validated HrEmployeePageReqVO reqVO) {
        PageResult<HrEmployeeDO> pageResult = hrEmployeeService.getEmployeePage(reqVO);
        PageResult<HrEmployeeRespVO> voResult = BeanUtils.toBean(pageResult, HrEmployeeRespVO.class);
        voResult.getList().forEach(this::fillUsername);
        // 列表中脱敏身份证
        if (!securityFrameworkService.hasPermission("system:hr-employee:sensitive")) {
            voResult.getList().forEach(vo -> {
                if (vo.getIdCard() != null && !vo.getIdCard().isEmpty()) {
                    vo.setIdCard(DesensitizedUtil.idCardNum(vo.getIdCard(), 4, 4));
                }
            });
        }
        return success(voResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出员工花名册")
    @PreAuthorize("@ss.hasPermission('system:hr-employee:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExcel(HttpServletResponse response, @Validated HrEmployeePageReqVO reqVO) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<HrEmployeeDO> list = hrEmployeeService.getEmployeePage(reqVO).getList();
        List<HrEmployeeRespVO> voList = BeanUtils.toBean(list, HrEmployeeRespVO.class);
        voList.forEach(this::fillUsername);
        ExcelUtils.write(response, "员工花名册.xls", "员工列表", HrEmployeeRespVO.class, voList);
    }

    @GetMapping("/my-profile")
    @Operation(summary = "个人中心 - 我的员工档案")
    public CommonResult<HrEmployeeRespVO> myProfile() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        HrEmployeeRespVO vo = hrEmployeeService.getMyProfile(userId);
        fillUsername(vo);
        return success(vo);
    }

    /** 根据 userId 填充 username，用于前端展示「姓名 (username)」。优先用系统用户昵称/账号，无则用花名册姓名避免 undefined */
    private void fillUsername(HrEmployeeRespVO vo) {
        if (vo == null) return;
        if (vo.getUserId() != null && vo.getUserId() > 0) {
            AdminUserDO user = adminUserService.getUser(vo.getUserId());
            String display = user != null ? (user.getNickname() != null && !user.getNickname().isEmpty() ? user.getNickname() : user.getUsername()) : null;
            vo.setUsername(display != null ? display : vo.getNickname());
        }
    }

}
