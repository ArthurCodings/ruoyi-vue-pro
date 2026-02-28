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

}
