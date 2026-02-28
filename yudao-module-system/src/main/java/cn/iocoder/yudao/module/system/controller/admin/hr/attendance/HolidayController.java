package cn.iocoder.yudao.module.system.controller.admin.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday.HolidayPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday.HolidayRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday.HolidaySaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.HolidayDO;
import cn.iocoder.yudao.module.system.service.hr.attendance.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - HR 法定节假日")
@RestController
@RequestMapping("/system/holiday")
@Validated
public class HolidayController {

    @Resource
    private HolidayService holidayService;

    @PostMapping("/create")
    @Operation(summary = "创建节假日")
    @PreAuthorize("@ss.hasPermission('system:holiday:update')")
    public CommonResult<Long> create(@Valid @RequestBody HolidaySaveReqVO reqVO) {
        return success(holidayService.createHoliday(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改节假日")
    @PreAuthorize("@ss.hasPermission('system:holiday:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody HolidaySaveReqVO reqVO) {
        holidayService.updateHoliday(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除节假日")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('system:holiday:update')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        holidayService.deleteHoliday(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取节假日详情")
    @Parameter(name = "id", description = "主键", required = true)
    @PreAuthorize("@ss.hasPermission('system:holiday:query')")
    public CommonResult<HolidayRespVO> get(@RequestParam("id") Long id) {
        HolidayDO holiday = holidayService.getHoliday(id);
        return success(BeanUtils.toBean(holiday, HolidayRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取节假日分页列表")
    @PreAuthorize("@ss.hasPermission('system:holiday:query')")
    public CommonResult<PageResult<HolidayRespVO>> page(@Validated HolidayPageReqVO reqVO) {
        PageResult<HolidayDO> pageResult = holidayService.getHolidayPage(reqVO);
        return success(BeanUtils.toBean(pageResult, HolidayRespVO.class));
    }

}
