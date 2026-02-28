package cn.iocoder.yudao.module.system.service.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceMonthlySummaryVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceRecordPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRecordDO;

import java.util.List;

public interface AttendanceRecordService {

    PageResult<AttendanceRecordDO> getAttendanceRecordPage(AttendanceRecordPageReqVO reqVO);

    /** 签到：返回打卡记录ID */
    Long clockIn(Long userId, String ip);

    /** 签退 */
    void clockOut(Long userId, String ip);

    /** 次日定时任务：批量计算前一天的考勤状态 */
    void calculateYesterdayStatus();

    /** 个人中心：本月每日打卡列表 */
    List<AttendanceRecordDO> getMyMonthlyRecords(Long userId, Integer yearMonth);

    /** 个人中心：本月考勤汇总 */
    AttendanceMonthlySummaryVO getMyMonthlySummary(Long userId, Integer yearMonth);

}
