package cn.iocoder.yudao.module.system.service.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday.HolidayPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday.HolidaySaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.HolidayDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.HolidayMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.HR_HOLIDAY_NOT_FOUND;

@Service
@Validated
public class HolidayServiceImpl implements HolidayService {

    @Resource
    private HolidayMapper holidayMapper;

    @Override
    public Long createHoliday(HolidaySaveReqVO createReqVO) {
        HolidayDO holiday = BeanUtils.toBean(createReqVO, HolidayDO.class);
        holidayMapper.insert(holiday);
        return holiday.getId();
    }

    @Override
    public void updateHoliday(HolidaySaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        HolidayDO updateObj = BeanUtils.toBean(updateReqVO, HolidayDO.class);
        holidayMapper.updateById(updateObj);
    }

    @Override
    public void deleteHoliday(Long id) {
        validateExists(id);
        holidayMapper.deleteById(id);
    }

    @Override
    public HolidayDO getHoliday(Long id) {
        return holidayMapper.selectById(id);
    }

    @Override
    public PageResult<HolidayDO> getHolidayPage(HolidayPageReqVO reqVO) {
        return holidayMapper.selectPage(reqVO);
    }

    @Override
    public List<HolidayDO> getHolidayListByYear(Integer year) {
        return holidayMapper.selectListByYear(year);
    }

    private void validateExists(Long id) {
        if (id != null && holidayMapper.selectById(id) == null) {
            throw exception(HR_HOLIDAY_NOT_FOUND);
        }
    }

}
