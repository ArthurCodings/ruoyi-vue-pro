package cn.iocoder.yudao.module.system.service.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceMonthlySummaryVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceRecordPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRecordDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRuleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceScheduleDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceRecordMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceRuleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceScheduleMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    @Resource
    private AttendanceRecordMapper recordMapper;
    @Resource
    private AttendanceScheduleMapper scheduleMapper;
    @Resource
    private AttendanceRuleMapper ruleMapper;

    @Override
    public PageResult<AttendanceRecordDO> getAttendanceRecordPage(AttendanceRecordPageReqVO reqVO) {
        return recordMapper.selectPage(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long clockIn(Long userId, String ip) {
        LocalDate today = LocalDate.now();
        // 校验今日排班
        AttendanceScheduleDO schedule = scheduleMapper.selectByUserAndDate(userId, today);
        if (schedule == null || !Boolean.TRUE.equals(schedule.getIsNeedClock())) {
            throw exception(HR_ATTENDANCE_TODAY_NO_NEED_CLOCK);
        }
        // 检查是否已签到
        AttendanceRecordDO existing = recordMapper.selectByUserAndDate(userId, today);
        if (existing != null && existing.getClockInTime() != null) {
            throw exception(HR_ATTENDANCE_ALREADY_CLOCK_IN);
        }
        if (existing != null) {
            existing.setClockInTime(LocalDateTime.now());
            existing.setClockInIp(ip);
            recordMapper.updateById(existing);
            return existing.getId();
        }
        AttendanceRecordDO record = new AttendanceRecordDO();
        record.setUserId(userId);
        record.setScheduleId(schedule.getId());
        record.setAttendanceDate(today);
        record.setClockInTime(LocalDateTime.now());
        record.setClockInIp(ip != null ? ip : "");
        record.setClockOutIp("");
        record.setStatus(0);
        record.setRemark("");
        recordMapper.insert(record);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clockOut(Long userId, String ip) {
        LocalDate today = LocalDate.now();
        AttendanceRecordDO record = recordMapper.selectByUserAndDate(userId, today);
        if (record == null || record.getClockInTime() == null) {
            throw exception(HR_ATTENDANCE_NOT_CLOCK_IN_YET);
        }
        if (record.getClockOutTime() != null) {
            throw exception(HR_ATTENDANCE_ALREADY_CLOCK_OUT);
        }
        record.setClockOutTime(LocalDateTime.now());
        record.setClockOutIp(ip != null ? ip : "");
        recordMapper.updateById(record);
    }

    @Override
    @Scheduled(cron = "0 10 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void calculateYesterdayStatus() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        int yearMonth = yesterday.getYear() * 100 + yesterday.getMonthValue();

        // 查出昨日所有需要打卡的排班
        List<AttendanceScheduleDO> schedules = scheduleMapper.selectListByMonth(yearMonth).stream()
                .filter(s -> s.getScheduleDate().equals(yesterday) && Boolean.TRUE.equals(s.getIsNeedClock()))
                .collect(Collectors.toList());
        if (schedules.isEmpty()) return;

        // 预加载规则
        Map<Long, AttendanceRuleDO> ruleMap = ruleMapper.selectListByStatus(0).stream()
                .collect(Collectors.toMap(AttendanceRuleDO::getId, r -> r));

        for (AttendanceScheduleDO schedule : schedules) {
            AttendanceRuleDO rule = ruleMap.get(schedule.getRuleId());
            if (rule == null) continue;

            AttendanceRecordDO record = recordMapper.selectByUserAndDate(schedule.getUserId(), yesterday);
            int status;
            if (record == null) {
                // 无打卡记录 => 缺勤，需新建
                record = new AttendanceRecordDO();
                record.setUserId(schedule.getUserId());
                record.setScheduleId(schedule.getId());
                record.setAttendanceDate(yesterday);
                record.setClockInIp("");
                record.setClockOutIp("");
                record.setRemark("");
                status = 3;
                record.setStatus(status);
                recordMapper.insert(record);
                continue;
            }

            if (record.getClockInTime() == null) {
                status = 3; // 缺勤
            } else if (record.getClockOutTime() == null) {
                status = 2; // 未签退视为早退
            } else {
                LocalTime clockIn = record.getClockInTime().toLocalTime();
                LocalTime clockOut = record.getClockOutTime().toLocalTime();
                LocalTime startTime = rule.getWorkStartTime();
                LocalTime endTime = rule.getWorkEndTime();

                boolean late = clockIn.isAfter(startTime.plusMinutes(rule.getLateThresholdMinutes()));
                boolean earlyLeave = clockOut.isBefore(endTime.minusMinutes(rule.getEarlyLeaveThresholdMinutes()));

                if (late) {
                    status = 1;
                } else if (earlyLeave) {
                    status = 2;
                } else {
                    status = 0;
                }
            }
            record.setStatus(status);
            recordMapper.updateById(record);
        }
        log.info("[calculateYesterdayStatus] 日期={} 处理 {} 条排班", yesterday, schedules.size());
    }

    @Override
    public List<AttendanceRecordDO> getMyMonthlyRecords(Long userId, Integer yearMonth) {
        return recordMapper.selectListByUserAndMonth(userId, yearMonth);
    }

    @Override
    public AttendanceMonthlySummaryVO getMyMonthlySummary(Long userId, Integer yearMonth) {
        int shouldAttend = scheduleMapper.countNeedClockByUserAndMonth(userId, yearMonth);
        int actualAttend = recordMapper.countByUserMonthAndStatusIn(userId, yearMonth, List.of(0, 1));
        int lateCount = recordMapper.countByUserMonthAndStatusIn(userId, yearMonth, List.of(1));
        int absentCount = recordMapper.countByUserMonthAndStatusIn(userId, yearMonth, List.of(3));

        // 构建每日状态列表
        List<AttendanceMonthlySummaryVO.DailyStatusVO> dailyList = new ArrayList<>();
        List<AttendanceScheduleDO> schedules = scheduleMapper.selectListByUserAndMonth(userId, yearMonth);
        Map<LocalDate, AttendanceRecordDO> recordMap = recordMapper.selectListByUserAndMonth(userId, yearMonth)
                .stream().collect(Collectors.toMap(AttendanceRecordDO::getAttendanceDate, r -> r, (a, b) -> a));

        for (AttendanceScheduleDO schedule : schedules) {
            int dayStatus;
            if (!Boolean.TRUE.equals(schedule.getIsNeedClock())) {
                dayStatus = -1; // 休息
            } else {
                AttendanceRecordDO record = recordMap.get(schedule.getScheduleDate());
                dayStatus = (record != null) ? record.getStatus() : 3;
            }
            dailyList.add(new AttendanceMonthlySummaryVO.DailyStatusVO(schedule.getScheduleDate(), dayStatus));
        }

        return new AttendanceMonthlySummaryVO(shouldAttend, actualAttend, lateCount, absentCount, dailyList);
    }

}
