package cn.iocoder.yudao.module.system.dal.mysql.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceSchedulePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceScheduleDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceScheduleMapper extends BaseMapperX<AttendanceScheduleDO> {

    default PageResult<AttendanceScheduleDO> selectPage(AttendanceSchedulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AttendanceScheduleDO>()
                .eqIfPresent(AttendanceScheduleDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AttendanceScheduleDO::getYearMonth, reqVO.getYearMonth())
                .eqIfPresent(AttendanceScheduleDO::getShiftType, reqVO.getShiftType())
                .orderByAsc(AttendanceScheduleDO::getScheduleDate));
    }

    default List<AttendanceScheduleDO> selectListByUserAndMonth(Long userId, Integer yearMonth) {
        return selectList(new LambdaQueryWrapperX<AttendanceScheduleDO>()
                .eq(AttendanceScheduleDO::getUserId, userId)
                .eq(AttendanceScheduleDO::getYearMonth, yearMonth)
                .orderByAsc(AttendanceScheduleDO::getScheduleDate));
    }

    default List<AttendanceScheduleDO> selectListByMonth(Integer yearMonth) {
        return selectList(new LambdaQueryWrapperX<AttendanceScheduleDO>()
                .eq(AttendanceScheduleDO::getYearMonth, yearMonth));
    }

    default AttendanceScheduleDO selectByUserAndDate(Long userId, LocalDate scheduleDate) {
        return selectOne(new LambdaQueryWrapperX<AttendanceScheduleDO>()
                .eq(AttendanceScheduleDO::getUserId, userId)
                .eq(AttendanceScheduleDO::getScheduleDate, scheduleDate));
    }

    default int countNeedClockByUserAndMonth(Long userId, Integer yearMonth) {
        return Math.toIntExact(selectCount(new LambdaQueryWrapperX<AttendanceScheduleDO>()
                .eq(AttendanceScheduleDO::getUserId, userId)
                .eq(AttendanceScheduleDO::getYearMonth, yearMonth)
                .eq(AttendanceScheduleDO::getIsNeedClock, true)));
    }

}
