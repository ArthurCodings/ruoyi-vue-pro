package cn.iocoder.yudao.module.infra.controller.admin.contract;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.contract.InfraContractDO;
import cn.iocoder.yudao.module.infra.service.contract.InfraContractService;
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

@Tag(name = "管理后台 - 合同管理")
@RestController
@RequestMapping("/infra/contract")
@Validated
public class InfraContractController {

    @Resource
    private InfraContractService contractService;

    @PostMapping("/create")
    @Operation(summary = "创建合同")
    @PreAuthorize("@ss.hasPermission('infra:contract:update')")
    public CommonResult<InfraContractRespVO> create(@Valid @RequestBody InfraContractSaveReqVO reqVO) {
        return success(contractService.createContract(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改合同")
    @PreAuthorize("@ss.hasPermission('infra:contract:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody InfraContractSaveReqVO reqVO) {
        contractService.updateContract(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除合同")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('infra:contract:update')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        contractService.deleteContract(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取合同详情")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('infra:contract:query')")
    public CommonResult<InfraContractRespVO> get(@RequestParam("id") Long id) {
        InfraContractDO contract = contractService.getContract(id);
        return success(BeanUtils.toBean(contract, InfraContractRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取合同分页列表")
    @PreAuthorize("@ss.hasPermission('infra:contract:query')")
    public CommonResult<PageResult<InfraContractRespVO>> page(@Validated InfraContractPageReqVO reqVO) {
        PageResult<InfraContractDO> pageResult = contractService.getContractPage(reqVO);
        return success(BeanUtils.toBean(pageResult, InfraContractRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出合同列表")
    @PreAuthorize("@ss.hasPermission('infra:contract:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExcel(HttpServletResponse response, @Validated InfraContractPageReqVO reqVO) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<InfraContractDO> list = contractService.getContractPage(reqVO).getList();
        ExcelUtils.write(response, "合同列表.xls", "合同数据", InfraContractRespVO.class,
                BeanUtils.toBean(list, InfraContractRespVO.class));
    }

    @GetMapping("/my-list")
    @Operation(summary = "个人中心 - 我负责的合同列表")
    public CommonResult<List<InfraContractRespVO>> myList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(contractService.getMyContractList(userId));
    }

}
