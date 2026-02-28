package cn.iocoder.yudao.module.infra.controller.admin.doc;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.*;
import cn.iocoder.yudao.module.infra.dal.dataobject.doc.InfraDocDO;
import cn.iocoder.yudao.module.infra.service.doc.InfraDocService;
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

@Tag(name = "管理后台 - 在线文档")
@RestController
@RequestMapping("/infra/doc")
@Validated
public class InfraDocController {

    @Resource
    private InfraDocService infraDocService;

    // ===== 分类管理 =====

    @PostMapping("/category/create")
    @Operation(summary = "创建文档分类")
    @PreAuthorize("@ss.hasPermission('infra:doc:update')")
    public CommonResult<Long> createCategory(@Valid @RequestBody DocCategorySaveReqVO reqVO) {
        return success(infraDocService.createCategory(reqVO));
    }

    @PutMapping("/category/update")
    @Operation(summary = "修改文档分类")
    @PreAuthorize("@ss.hasPermission('infra:doc:update')")
    public CommonResult<Boolean> updateCategory(@Valid @RequestBody DocCategorySaveReqVO reqVO) {
        infraDocService.updateCategory(reqVO);
        return success(true);
    }

    @DeleteMapping("/category/delete")
    @Operation(summary = "删除文档分类")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('infra:doc:update')")
    public CommonResult<Boolean> deleteCategory(@RequestParam("id") Long id) {
        infraDocService.deleteCategory(id);
        return success(true);
    }

    @GetMapping("/category/tree")
    @Operation(summary = "获取文档分类树")
    @PreAuthorize("@ss.hasPermission('infra:doc:query')")
    public CommonResult<List<DocCategoryRespVO>> getCategoryTree() {
        return success(infraDocService.getCategoryTree());
    }

    // ===== 文档管理 =====

    @PostMapping("/create")
    @Operation(summary = "创建文档")
    @PreAuthorize("@ss.hasPermission('infra:doc:update')")
    public CommonResult<Long> create(@Valid @RequestBody InfraDocSaveReqVO reqVO) {
        return success(infraDocService.createDoc(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改文档")
    @PreAuthorize("@ss.hasPermission('infra:doc:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody InfraDocSaveReqVO reqVO) {
        infraDocService.updateDoc(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文档")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('infra:doc:update')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        infraDocService.deleteDoc(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取文档详情")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('infra:doc:query')")
    public CommonResult<InfraDocRespVO> get(@RequestParam("id") Long id) {
        infraDocService.incrementViewCount(id);
        InfraDocDO doc = infraDocService.getDoc(id);
        return success(BeanUtils.toBean(doc, InfraDocRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取文档分页列表")
    @PreAuthorize("@ss.hasPermission('infra:doc:query')")
    public CommonResult<PageResult<InfraDocRespVO>> page(@Validated InfraDocPageReqVO reqVO) {
        PageResult<InfraDocDO> pageResult = infraDocService.getDocPage(reqVO);
        return success(BeanUtils.toBean(pageResult, InfraDocRespVO.class));
    }

}
