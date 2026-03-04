package cn.iocoder.yudao.module.system.dal.mysql.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceRecordPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Mapper
public interface AttendanceRecordMapper extends BaseMapperX<AttendanceRecordDO> {

    default PageResult<AttendanceRecordDO> selectPage(AttendanceRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AttendanceRecordDO>()
                .eqIfPresent(AttendanceRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AttendanceRecordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AttendanceRecordDO::getAttendanceDate, reqVO.getBeginDate(), reqVO.getEndDate())
                .orderByDesc(AttendanceRecordDO::getAttendanceDate));
    }

    default AttendanceRecordDO selectByUserAndDate(Long userId, LocalDate date) {
        return selectOne(new LambdaQueryWrapperX<AttendanceRecordDO>()
                .eq(AttendanceRecordDO::getUserId, userId)
                .eq(AttendanceRecordDO::getAttendanceDate, date));
    }

    default List<AttendanceRecordDO> selectListByUserAndMonth(Long userId, Integer yearMonth) {
        LocalDate start = LocalDate.of(yearMonth / 100, yearMonth % 100, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return selectList(new LambdaQueryWrapperX<AttendanceRecordDO>()
                .eq(AttendanceRecordDO::getUserId, userId)
                .between(AttendanceRecordDO::getAttendanceDate, start, end)
                .orderByAsc(AttendanceRecordDO::getAttendanceDate));
    }

    default int countByUserMonthAndStatusIn(Long userId, Integer yearMonth, Collection<Integer> statuses) {
        LocalDate start = LocalDate.of(yearMonth / 100, yearMonth % 100, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return Math.toIntExact(selectCount(new LambdaQueryWrapperX<AttendanceRecordDO>()
                .eq(AttendanceRecordDO::getUserId, userId)
                .between(AttendanceRecordDO::getAttendanceDate, start, end)
                .in(AttendanceRecordDO::getStatus, statuses)));
    }

    default int countByUserMonthAndStatus(Long userId, Integer yearMonth, Integer status) {
        LocalDate start = LocalDate.of(yearMonth / 100, yearMonth % 100, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return Math.toIntExact(selectCount(new LambdaQueryWrapperX<AttendanceRecordDO>()
                .eq(AttendanceRecordDO::getUserId, userId)
                .between(AttendanceRecordDO::getAttendanceDate, start, end)
                .eq(AttendanceRecordDO::getStatus, status)));
    }

    default List<AttendanceRecordDO> selectListByUserAndDateRange(Long userId, LocalDate start, LocalDate end) {
        return selectList(new LambdaQueryWrapperX<AttendanceRecordDO>()
                .eq(AttendanceRecordDO::getUserId, userId)
                .between(AttendanceRecordDO::getAttendanceDate, start, end)
                .orderByAsc(AttendanceRecordDO::getAttendanceDate));
    }

}
