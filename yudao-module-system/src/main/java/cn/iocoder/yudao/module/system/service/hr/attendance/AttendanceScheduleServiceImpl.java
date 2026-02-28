package cn.iocoder.yudao.module.system.service.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceSchedulePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceScheduleRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.schedule.AttendanceScheduleUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRuleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceScheduleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.HolidayDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceRuleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceScheduleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.HolidayMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.employee.HrEmployeeMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class AttendanceScheduleServiceImpl implements AttendanceScheduleService {

    @Resource
    private AttendanceScheduleMapper scheduleMapper;
    @Resource
    private AttendanceRuleMapper ruleMapper;
    @Resource
    private HolidayMapper holidayMapper;
    @Resource
    private HrEmployeeMapper employeeMapper;

    @Override
    public PageResult<AttendanceScheduleDO> getAttendanceSchedulePage(AttendanceSchedulePageReqVO reqVO) {
        return scheduleMapper.selectPage(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateMonthlySchedule(Integer yearMonth) {
        if (yearMonth == null) {
            // 定时任务时自动取下个月
            YearMonth next = YearMonth.now().plusMonths(1);
            yearMonth = next.getYear() * 100 + next.getMonthValue();
        }
        int year = yearMonth / 100;
        int month = yearMonth % 100;
        YearMonth ym = YearMonth.of(year, month);

        // 查询在职员工
        List<HrEmployeeDO> employees = employeeMapper.selectListByStatus(1);
        if (employees.isEmpty()) {
            log.info("[generateMonthlySchedule] 无在职员工，跳过");
            return;
        }

        // 加载全部启用规则，按 id 分组
        List<AttendanceRuleDO> rules = ruleMapper.selectListByStatus(0);
        Map<Long, AttendanceRuleDO> ruleMap = rules.stream()
                .collect(Collectors.toMap(AttendanceRuleDO::getId, r -> r));
        // 获取默认晚班规则
        AttendanceRuleDO defaultRule = rules.stream()
                .filter(r -> Integer.valueOf(2).equals(r.getShiftType()))
                .findFirst()
                .orElseThrow(() -> exception(HR_ATTENDANCE_RULE_NOT_FOUND));

        // 加载当月节假日，按日期分组
        List<HolidayDO> holidays = holidayMapper.selectListByYear(year);
        Map<LocalDate, HolidayDO> holidayMap = holidays.stream()
                .collect(Collectors.toMap(HolidayDO::getHolidayDate, h -> h, (a, b) -> a));

        List<AttendanceScheduleDO> toInsert = new ArrayList<>();
        int totalDays = ym.lengthOfMonth();

        for (HrEmployeeDO emp : employees) {
            // 确定该员工使用的规则
            AttendanceRuleDO rule = (emp.getDefaultRuleId() != null && emp.getDefaultRuleId() > 0)
                    ? ruleMap.getOrDefault(emp.getDefaultRuleId(), defaultRule)
                    : defaultRule;

            for (int day = 1; day <= totalDays; day++) {
                LocalDate date = LocalDate.of(year, month, day);
                AttendanceScheduleDO schedule = new AttendanceScheduleDO();
                schedule.setUserId(emp.getUserId());
                schedule.setRuleId(rule.getId());
                schedule.setShiftType(rule.getShiftType());
                schedule.setScheduleDate(date);
                schedule.setYearMonth(yearMonth);

                HolidayDO holiday = holidayMap.get(date);
                if (holiday != null && holiday.getType() == 1) {
                    // 节假日休息
                    schedule.setDayType(3);
                    schedule.setIsNeedClock(false);
                } else if (holiday != null && holiday.getType() == 2) {
                    // 调班工作日
                    schedule.setDayType(4);
                    schedule.setIsNeedClock(true);
                } else {
                    int dayOfWeek = date.getDayOfWeek().getValue(); // 1=周一 ... 7=周日
                    boolean isWeekend = (rule.getWorkDaysPerWeek() == 5 && dayOfWeek >= 6)
                            || (rule.getWorkDaysPerWeek() == 6 && dayOfWeek == 7);
                    if (isWeekend) {
                        schedule.setDayType(2);
                        schedule.setIsNeedClock(false);
                    } else {
                        schedule.setDayType(1);
                        schedule.setIsNeedClock(true);
                    }
                }
                toInsert.add(schedule);
            }
        }

        // 批量插入，利用 UNIQUE KEY 跳过已存在的记录
        scheduleMapper.insertBatch(toInsert);
        log.info("[generateMonthlySchedule] 年月={} 生成排班 {} 条", yearMonth, toInsert.size());
    }

    /** 定时任务：每月25日凌晨1点自动生成下月排班（无参方法才能用 @Scheduled） */
    @Scheduled(cron = "0 0 1 25 * ?")
    public void scheduledGenerateNextMonth() {
        generateMonthlySchedule(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScheduleRule(AttendanceScheduleUpdateReqVO updateReqVO) {
        AttendanceScheduleDO schedule = scheduleMapper.selectById(updateReqVO.getId());
        if (schedule == null) {
            throw exception(HR_ATTENDANCE_SCHEDULE_NOT_FOUND);
        }
        AttendanceRuleDO rule = ruleMapper.selectById(updateReqVO.getRuleId());
        if (rule == null) {
            throw exception(HR_ATTENDANCE_RULE_NOT_FOUND);
        }
        schedule.setRuleId(rule.getId());
        schedule.setShiftType(rule.getShiftType());
        scheduleMapper.updateById(schedule);
    }

    @Override
    public List<AttendanceScheduleRespVO> getMyMonthlySchedule(Long userId, Integer yearMonth) {
        List<AttendanceScheduleDO> list = scheduleMapper.selectListByUserAndMonth(userId, yearMonth);
        return BeanUtils.toBean(list, AttendanceScheduleRespVO.class);
    }

    @Override
    public int countMyMonthShouldAttend(Long userId, Integer yearMonth) {
        return scheduleMapper.countNeedClockByUserAndMonth(userId, yearMonth);
    }

}
