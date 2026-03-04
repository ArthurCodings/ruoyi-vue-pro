package cn.iocoder.yudao.module.system.dal.mysql.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.holiday.HolidayPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.HolidayDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HolidayMapper extends BaseMapperX<HolidayDO> {

    default PageResult<HolidayDO> selectPage(HolidayPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HolidayDO>()
                .eqIfPresent(HolidayDO::getYear, reqVO.getYear())
                .eqIfPresent(HolidayDO::getType, reqVO.getType())
                .likeIfPresent(HolidayDO::getName, reqVO.getName())
                .orderByAsc(HolidayDO::getHolidayDate));
    }

    default List<HolidayDO> selectListByYear(Integer year) {
        return selectList(HolidayDO::getYear, year);
    }

    /**
     * 查询某月的法定节假日休息日（type=1），用于日薪计算中扣除假期
     * yearMonth 格式：202603
     */
    default List<HolidayDO> selectRestDaysByMonth(Integer yearMonth) {
        java.time.LocalDate start = java.time.LocalDate.of(yearMonth / 100, yearMonth % 100, 1);
        java.time.LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return selectList(new LambdaQueryWrapperX<HolidayDO>()
                .eq(HolidayDO::getType, 1)
                .between(HolidayDO::getHolidayDate, start, end));
    }

}
