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

    /**
     * 流程审批通过后，将请假日期区间内的考勤记录更新为对应请假状态
     * 若该日期无考勤记录（如本来是缺勤），则自动创建一条
     *
     * @param userId            员工用户ID
     * @param startDateStr      请假开始日期（yyyy-MM-dd）
     * @param endDateStr        请假结束日期（yyyy-MM-dd）
     * @param status            考勤状态（6=病假 7=事假）
     * @param leaveType         请假类型（1=病假 2=事假）
     * @param processInstanceId 来源流程实例ID
     */
    void updateLeaveStatus(Long userId, String startDateStr, String endDateStr,
                           int status, int leaveType, String processInstanceId);

    /**
     * 流程审批通过后，处理补打卡申请
     * 根据补卡类型写入/更新当天的签到或签退时间，并重新计算考勤状态
     *
     * @param userId            员工用户ID
     * @param supplementDateStr 补打卡日期（yyyy-MM-dd）
     * @param clockType         补卡类型（"1"=签到 "2"=签退 "3"=签到+签退）
     * @param clockInTimeStr    实际签到时间（HH:mm:ss，clockType=1或3时有效）
     * @param clockOutTimeStr   实际签退时间（HH:mm:ss，clockType=2或3时有效）
     * @param processInstanceId 来源流程实例ID
     */
    void supplementClock(Long userId, String supplementDateStr, String clockType,
                         String clockInTimeStr, String clockOutTimeStr, String processInstanceId);

}
