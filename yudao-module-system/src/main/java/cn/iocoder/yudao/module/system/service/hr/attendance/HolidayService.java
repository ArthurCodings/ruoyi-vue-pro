package cn.iocoder.yudao.module.system.service.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday.HolidayPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday.HolidaySaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.HolidayDO;

import java.util.List;

public interface HolidayService {

    Long createHoliday(HolidaySaveReqVO createReqVO);

    void updateHoliday(HolidaySaveReqVO updateReqVO);

    void deleteHoliday(Long id);

    HolidayDO getHoliday(Long id);

    PageResult<HolidayDO> getHolidayPage(HolidayPageReqVO reqVO);

    List<HolidayDO> getHolidayListByYear(Integer year);

}
