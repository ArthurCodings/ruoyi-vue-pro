package cn.iocoder.yudao.module.system.service.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceSchedulePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceScheduleRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceScheduleUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceScheduleDO;

import java.util.List;

public interface AttendanceScheduleService {

    PageResult<AttendanceScheduleDO> getAttendanceSchedulePage(AttendanceSchedulePageReqVO reqVO);

    /**
     * 批量生成指定年月的排班（按规则+节假日自动生成）
     * @param yearMonth 目标年月，如 202603
     */
    void generateMonthlySchedule(Integer yearMonth);

    /**
     * 调班：修改某员工某天的排班规则
     */
    void updateScheduleRule(AttendanceScheduleUpdateReqVO updateReqVO);

    List<AttendanceScheduleRespVO> getMyMonthlySchedule(Long userId, Integer yearMonth);

    int countMyMonthShouldAttend(Long userId, Integer yearMonth);

}
