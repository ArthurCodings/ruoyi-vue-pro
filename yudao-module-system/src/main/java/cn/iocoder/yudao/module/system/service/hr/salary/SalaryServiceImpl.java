package cn.iocoder.yudao.module.system.service.hr.salary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.service.contract.InfraContractService;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalaryPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalarySaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.HolidayDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.PerformanceScoreDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.EmployeeSalaryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryConfigDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryConfirmNoticeDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryMonthlyDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceRecordMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceScheduleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.HolidayMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.employee.HrEmployeeMapper;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.PerformanceTemplateDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.performance.PerformanceScoreMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.performance.PerformanceTemplateMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.EmployeeSalaryMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.SalaryConfigMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.SalaryConfirmNoticeMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.SalaryMonthlyMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class SalaryServiceImpl implements SalaryService {

    /** 迟到扣款：超过1次后每次扣30元 */
    private static final BigDecimal LATE_DEDUCTION_PER_TIME = new BigDecimal("30");
    /** 病假工资保留比例（获得80%，扣除20%） */
    private static final BigDecimal SICK_LEAVE_DEDUCTION_RATIO = new BigDecimal("0.2");
    @Resource private SalaryConfigMapper salaryConfigMapper;
    @Resource private EmployeeSalaryMapper employeeSalaryMapper;
    @Resource private SalaryMonthlyMapper monthlyMapper;
    @Resource private HrEmployeeMapper employeeMapper;
    @Resource private AttendanceScheduleMapper scheduleMapper;
    @Resource private AttendanceRecordMapper recordMapper;
    @Resource private HolidayMapper holidayMapper;
    @Resource private InfraContractService contractService;
    @Resource private PerformanceScoreMapper performanceScoreMapper;
    @Resource private PerformanceTemplateMapper performanceTemplateMapper;
    @Resource private SalaryConfirmNoticeMapper confirmNoticeMapper;
    @Resource private ObjectMapper objectMapper;

    // ===== 薪资配置 =====

    @Override
    public SalaryConfigRespVO getSalaryConfig() {
        SalaryConfigDO config = salaryConfigMapper.selectFirst();
        return BeanUtils.toBean(config, SalaryConfigRespVO.class);
    }

    @Override
    public void saveSalaryConfig(SalaryConfigSaveReqVO saveReqVO) {
        SalaryConfigDO existing = salaryConfigMapper.selectFirst();
        if (existing == null) {
            salaryConfigMapper.insert(BeanUtils.toBean(saveReqVO, SalaryConfigDO.class));
        } else {
            SalaryConfigDO update = BeanUtils.toBean(saveReqVO, SalaryConfigDO.class);
            update.setId(existing.getId());
            salaryConfigMapper.updateById(update);
        }
    }

    // ===== 员工薪资档案 =====

    @Override
    public Long createEmployeeSalary(EmployeeSalarySaveReqVO createReqVO) {
        EmployeeSalaryDO obj = BeanUtils.toBean(createReqVO, EmployeeSalaryDO.class);
        employeeSalaryMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateEmployeeSalary(EmployeeSalarySaveReqVO updateReqVO) {
        if (employeeSalaryMapper.selectById(updateReqVO.getId()) == null) {
            throw exception(HR_EMPLOYEE_SALARY_NOT_FOUND);
        }
        employeeSalaryMapper.updateById(BeanUtils.toBean(updateReqVO, EmployeeSalaryDO.class));
    }

    @Override
    public EmployeeSalaryDO getEmployeeSalary(Long id) {
        return employeeSalaryMapper.selectById(id);
    }

    @Override
    public PageResult<EmployeeSalaryDO> getEmployeeSalaryPage(EmployeeSalaryPageReqVO reqVO) {
        return employeeSalaryMapper.selectPage(reqVO);
    }

    @Override
    public EmployeeSalaryDO getEmployeeSalaryByUserId(Long userId) {
        return employeeSalaryMapper.selectByUserId(userId);
    }

    // ===== 月度薪资 =====

    @Override
    public PageResult<SalaryMonthlyDO> getSalaryMonthlyPage(SalaryMonthlyPageReqVO reqVO) {
        return monthlyMapper.selectPage(reqVO);
    }

    @Override
    public SalaryMonthlyDO getSalaryMonthlyById(Long id) {
        return monthlyMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateMonthly(Integer yearMonth) {
        SalaryConfigDO config = salaryConfigMapper.selectFirst();
        if (config == null) throw exception(HR_SALARY_CONFIG_NOT_FOUND);

        int year  = yearMonth / 100;
        int month = yearMonth % 100;
        YearMonth ym = YearMonth.of(year, month);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd   = ym.atEndOfMonth();

        // 日薪分母 = 当月总天数 - 周日天数 - 法定节假日休息天数（与周日重叠的不重复计算）
        int totalDays   = ym.lengthOfMonth();
        int sundayCount = countSundaysInMonth(monthStart, monthEnd);
        int holidayRestDays = countHolidayRestDaysExcludeSunday(yearMonth, monthStart, monthEnd);
        int workingDays = totalDays - sundayCount - holidayRestDays;
        if (workingDays <= 0) workingDays = 1; // 防止除零

        List<HrEmployeeDO> employees = employeeMapper.selectListByStatus(1);
        int generated = 0;
        for (HrEmployeeDO emp : employees) {
            // 重置逻辑：已有记录且状态为待确认(0)时，物理删除后重算；已确认(1)/已发放(2)则跳过
            SalaryMonthlyDO existing = monthlyMapper.selectByUserAndMonth(emp.getUserId(), yearMonth);
            if (existing != null) {
                if (existing.getStatus() != null && existing.getStatus() != 0) {
                    log.info("[generateMonthly] 用户 {} 的 {} 月薪资已确认/已发放，跳过重算", emp.getUserId(), yearMonth);
                    continue;
                }
                monthlyMapper.physicalDeleteByUserAndMonth(emp.getUserId(), yearMonth);
            }

            EmployeeSalaryDO salaryRecord = employeeSalaryMapper.selectByUserId(emp.getUserId());
            BigDecimal baseSalary     = salaryRecord != null ? salaryRecord.getBaseSalary()     : BigDecimal.ZERO;
            BigDecimal positionSalary = salaryRecord != null ? salaryRecord.getPositionSalary() : BigDecimal.ZERO;
            BigDecimal allowance      = salaryRecord != null ? salaryRecord.getAllowance()      : BigDecimal.ZERO;

            // 日薪 = (基本工资 + 岗位工资 + 补贴) / 计薪工作日，四舍五入保留2位
            BigDecimal salaryBase = baseSalary.add(positionSalary).add(allowance);
            BigDecimal dailySalary = workingDays > 0
                    ? salaryBase.divide(BigDecimal.valueOf(workingDays), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            // 考勤数据统计
            int shouldAttend = scheduleMapper.countNeedClockByUserAndMonth(emp.getUserId(), yearMonth);
            int actualAttend = recordMapper.countByUserMonthAndStatusIn(emp.getUserId(), yearMonth, Arrays.asList(0, 1, 6, 7));
            int lateCount    = recordMapper.countByUserMonthAndStatus(emp.getUserId(), yearMonth, 1);
            // 缺勤 = 应出勤 - 实际出勤（按整月计算，未到日期也算缺勤）
            int absentCount  = Math.max(0, shouldAttend - actualAttend);
            int sickLeaveDays   = recordMapper.countByUserMonthAndStatus(emp.getUserId(), yearMonth, 6);
            int casualLeaveDays = recordMapper.countByUserMonthAndStatus(emp.getUserId(), yearMonth, 7);

            // 迟到扣款：每月允许1次迟到，第2次起每次扣30元
            BigDecimal lateDeduction = LATE_DEDUCTION_PER_TIME
                    .multiply(BigDecimal.valueOf(Math.max(0, lateCount - 1)));

            // 病假扣款：病假工资为正常出勤的0.8倍，即扣20%日薪 × 天数
            BigDecimal sickLeaveDeduction = dailySalary
                    .multiply(BigDecimal.valueOf(sickLeaveDays))
                    .multiply(SICK_LEAVE_DEDUCTION_RATIO)
                    .setScale(2, RoundingMode.HALF_UP);

            // 事假扣款：直接扣除当天工资（日薪 × 天数）
            BigDecimal casualLeaveDeduction = dailySalary
                    .multiply(BigDecimal.valueOf(casualLeaveDays))
                    .setScale(2, RoundingMode.HALF_UP);

            // 缺勤扣款：直接扣除当天工资（日薪 × 缺勤天数）
            BigDecimal absentDeduction = dailySalary
                    .multiply(BigDecimal.valueOf(absentCount))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal attendanceDeduction = lateDeduction
                    .add(sickLeaveDeduction)
                    .add(casualLeaveDeduction)
                    .add(absentDeduction);

            // 全勤奖：缺勤=0 且 迟到次数≤1 时发放（每月允许1次迟到不扣全勤）
            // 优先使用个人配置，0则使用全局配置
            BigDecimal personalBonus = (salaryRecord != null && salaryRecord.getFullAttendanceAmount() != null)
                    ? salaryRecord.getFullAttendanceAmount() : BigDecimal.ZERO;
            BigDecimal configBonus   = config.getFullAttendanceAmount() != null ? config.getFullAttendanceAmount() : BigDecimal.ZERO;
            BigDecimal fullBonusAmount = personalBonus.compareTo(BigDecimal.ZERO) > 0 ? personalBonus : configBonus;
            boolean fullAttend = absentCount == 0 && lateCount <= 1 && Boolean.TRUE.equals(config.getEnableFullAttendance());
            BigDecimal fullBonus = fullAttend ? fullBonusAmount : BigDecimal.ZERO;

            // 绩效：performance_source=1 时从绩效打分表取，公式 = 基本薪资 × 系数(0.45) × 绩效系数
            BigDecimal performance = BigDecimal.ZERO;
            if (Integer.valueOf(1).equals(config.getPerformanceSource())) {
                PerformanceScoreDO scoreRecord = performanceScoreMapper.selectByUserAndMonth(emp.getUserId(), yearMonth);
                if (scoreRecord != null && Integer.valueOf(2).equals(scoreRecord.getStatus())) {
                    PerformanceTemplateDO template = performanceTemplateMapper.selectById(scoreRecord.getTemplateId());
                    BigDecimal ratio = template != null && template.getPerformanceBaseRatio() != null
                            ? template.getPerformanceBaseRatio() : new BigDecimal("0.45");
                    BigDecimal coeff = scoreRecord.getPerformanceCoefficient() != null
                            ? scoreRecord.getPerformanceCoefficient() : BigDecimal.ZERO;
                    performance = baseSalary.multiply(ratio).multiply(coeff).setScale(2, RoundingMode.HALF_UP);
                }
            }

            // 提成：按时间比例从合同取
            BigDecimal commission = BigDecimal.ZERO;
            if (Boolean.TRUE.equals(config.getEnableCommission())
                    && Integer.valueOf(1).equals(config.getCommissionSource())) {
                commission = contractService.sumCommissionByUserAndMonth(emp.getUserId(), yearMonth);
            }

            // 构建月度薪资记录
            SalaryMonthlyDO monthly = new SalaryMonthlyDO();
            monthly.setUserId(emp.getUserId());
            monthly.setYearMonth(yearMonth);
            monthly.setBaseSalary(baseSalary);
            monthly.setPositionSalary(positionSalary);
            monthly.setAllowance(allowance);
            monthly.setPerformance(performance);
            monthly.setCommission(commission);
            monthly.setDailySalary(dailySalary);
            monthly.setFullAttendanceBonus(fullBonus);
            monthly.setLateDeduction(lateDeduction);
            monthly.setSickLeaveDays(sickLeaveDays);
            monthly.setSickLeaveDeduction(sickLeaveDeduction);
            monthly.setCasualLeaveDays(casualLeaveDays);
            monthly.setCasualLeaveDeduction(casualLeaveDeduction);
            monthly.setAbsentDeduction(absentDeduction);
            monthly.setAttendanceDeduction(attendanceDeduction);
            monthly.setShouldAttendDays(shouldAttend);
            monthly.setActualAttendDays(actualAttend);
            monthly.setLateCount(lateCount);
            monthly.setAbsentCount(absentCount);
            monthly.setConfirmNoticeId(0L);
            monthly.setStatus(0);
            monthly.setRemark("");

            // 应发合计 = 基本 + 岗位 + 绩效 + 提成 + 补贴 + 全勤奖 - 考勤扣款
            BigDecimal total = baseSalary
                    .add(positionSalary)
                    .add(performance)
                    .add(commission)
                    .add(allowance)
                    .add(fullBonus)
                    .subtract(attendanceDeduction)
                    .setScale(2, RoundingMode.HALF_UP);
            // 最低不低于0
            if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
            monthly.setTotalSalary(total);

            // 构建薪资明细JSON（供前端查看明细弹窗使用）
            monthly.setSalaryDetail(buildSalaryDetail(monthly, totalDays, sundayCount, holidayRestDays, workingDays));

            monthlyMapper.insert(monthly);
            generated++;
        }
        log.info("[generateMonthly] 年月={} 共生成/重算 {} 条薪资草稿", yearMonth, generated);
    }

    /** 计算当月周日数量 */
    private int countSundaysInMonth(LocalDate monthStart, LocalDate monthEnd) {
        int count = 0;
        LocalDate cur = monthStart;
        while (!cur.isAfter(monthEnd)) {
            if (cur.getDayOfWeek() == DayOfWeek.SUNDAY) count++;
            cur = cur.plusDays(1);
        }
        return count;
    }

    /**
     * 计算当月法定节假日休息天数（type=1），排除与周日重叠的日期
     */
    private int countHolidayRestDaysExcludeSunday(Integer yearMonth, LocalDate monthStart, LocalDate monthEnd) {
        List<HolidayDO> holidays = holidayMapper.selectRestDaysByMonth(yearMonth);
        long count = holidays.stream()
                .map(HolidayDO::getHolidayDate)
                .filter(d -> !d.isAfter(monthEnd) && !d.isBefore(monthStart))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .distinct()
                .count();
        return (int) count;
    }

    /**
     * 构建薪资明细JSON，每项含计算过程说明，供前端「薪资计算明细」弹窗展示
     * 结构：{ "items": [ { "item", "label", "amount", "description" }, ... ] }
     */
    private String buildSalaryDetail(SalaryMonthlyDO monthly, int totalDays, int sundayDays,
                                     int holidayDays, int workingDays) {
        try {
            BigDecimal base = nvl(monthly.getBaseSalary());
            BigDecimal daily = nvl(monthly.getDailySalary());
            int lateCount = monthly.getLateCount() != null ? monthly.getLateCount() : 0;
            BigDecimal lateDed = nvl(monthly.getLateDeduction());
            BigDecimal sickDed = nvl(monthly.getSickLeaveDeduction());
            BigDecimal casualDed = nvl(monthly.getCasualLeaveDeduction());
            BigDecimal absentDed = nvl(monthly.getAbsentDeduction());
            BigDecimal attendDed = nvl(monthly.getAttendanceDeduction());

            List<Map<String, Object>> items = new ArrayList<>();
            addItem(items, "totalDays", "当月总天数", totalDays, "");
            addItem(items, "sundayDays", "周日天数", sundayDays, "");
            addItem(items, "holidayDays", "法定节假日休息天数", holidayDays, "与周日重叠的不重复计算");
            addItem(items, "workingDays", "计薪工作日", workingDays,
                    String.format("总天数 - 周日 - 法定节假日 = %d - %d - %d = %d", totalDays, sundayDays, holidayDays, workingDays));
            addItem(items, "baseSalary", "基本工资", base, "来自员工薪资档案");
            addItem(items, "dailySalary", "日薪", daily,
                    String.format("(基本工资+岗位工资+补贴) ÷ 计薪工作日 = (%s+%s+%s) ÷ %d = %s",
                            fmt(base), fmt(nvl(monthly.getPositionSalary())), fmt(nvl(monthly.getAllowance())), workingDays, fmt(daily)));
            addItem(items, "positionSalary", "岗位工资", nvl(monthly.getPositionSalary()), "来自员工薪资档案");
            addItem(items, "performance", "绩效", nvl(monthly.getPerformance()), "基本薪资 × 0.45 × 绩效系数（审核通过后取值）");
            addItem(items, "commission", "提成", nvl(monthly.getCommission()), "按归属期涉及的月份个数平均分配");
            addItem(items, "allowance", "补贴", nvl(monthly.getAllowance()), "来自员工薪资档案");
            addItem(items, "fullAttendanceBonus", "全勤奖", nvl(monthly.getFullAttendanceBonus()), "缺勤=0 且 迟到≤1 时发放");
            addItem(items, "shouldAttendDays", "应出勤天数", monthly.getShouldAttendDays() != null ? monthly.getShouldAttendDays() : 0, "排班中需打卡的工作日数");
            addItem(items, "actualAttendDays", "实际出勤天数", monthly.getActualAttendDays() != null ? monthly.getActualAttendDays() : 0, "正常+迟到+病假+事假");
            addItem(items, "lateCount", "迟到次数", lateCount, "");
            addItem(items, "lateDeduction", "迟到扣款", lateDed,
                    String.format("第2次起每次扣30元 = max(0,%d-1)×30 = %s", lateCount, fmt(lateDed)));
            addItem(items, "sickLeaveDays", "病假天数", monthly.getSickLeaveDays() != null ? monthly.getSickLeaveDays() : 0, "流程审批通过后统计");
            addItem(items, "sickLeaveDeduction", "病假扣款", sickDed,
                    String.format("日薪 × 病假天数 × 20%% = %s × %d × 0.2 = %s", fmt(daily), monthly.getSickLeaveDays() != null ? monthly.getSickLeaveDays() : 0, fmt(sickDed)));
            addItem(items, "casualLeaveDays", "事假天数", monthly.getCasualLeaveDays() != null ? monthly.getCasualLeaveDays() : 0, "流程审批通过后统计");
            addItem(items, "casualLeaveDeduction", "事假扣款", casualDed,
                    String.format("日薪 × 事假天数 = %s × %d = %s", fmt(daily), monthly.getCasualLeaveDays() != null ? monthly.getCasualLeaveDays() : 0, fmt(casualDed)));
            addItem(items, "absentCount", "缺勤天数", monthly.getAbsentCount() != null ? monthly.getAbsentCount() : 0, "应出勤 - 实际出勤（按整月计算）");
            addItem(items, "absentDeduction", "缺勤扣款", absentDed,
                    String.format("日薪 × 缺勤天数 = %s × %d = %s", fmt(daily), monthly.getAbsentCount() != null ? monthly.getAbsentCount() : 0, fmt(absentDed)));
            addItem(items, "attendanceDeduction", "考勤扣款合计", attendDed,
                    String.format("迟到+病假+事假+缺勤 = %s+%s+%s+%s = %s", fmt(lateDed), fmt(sickDed), fmt(casualDed), fmt(absentDed), fmt(attendDed)));
            addItem(items, "totalSalary", "应发合计", nvl(monthly.getTotalSalary()),
                    String.format("基本+岗位+绩效+提成+补贴+全勤奖-考勤扣款 = %s+%s+%s+%s+%s+%s-%s = %s",
                            fmt(nvl(monthly.getBaseSalary())), fmt(nvl(monthly.getPositionSalary())), fmt(nvl(monthly.getPerformance())),
                            fmt(nvl(monthly.getCommission())), fmt(nvl(monthly.getAllowance())), fmt(nvl(monthly.getFullAttendanceBonus())),
                            fmt(attendDed), fmt(nvl(monthly.getTotalSalary()))));

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("items", items);
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.warn("[buildSalaryDetail] 序列化明细JSON失败", e);
            return "{}";
        }
    }

    private BigDecimal nvl(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private String fmt(BigDecimal v) {
        return v != null ? v.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
    }

    private void addItem(List<Map<String, Object>> items, String item, String label, Object amount, String description) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("item", item);
        m.put("label", label);
        m.put("amount", amount instanceof BigDecimal ? ((BigDecimal) amount).setScale(2, RoundingMode.HALF_UP) : amount);
        m.put("description", description != null ? description : "");
        items.add(m);
    }

    @Override
    public void updateSalaryMonthly(SalaryMonthlyUpdateReqVO updateReqVO) {
        SalaryMonthlyDO monthly = monthlyMapper.selectById(updateReqVO.getId());
        if (monthly == null) throw exception(HR_SALARY_MONTHLY_NOT_FOUND);
        if (monthly.getStatus() >= 1) throw exception(HR_SALARY_MONTHLY_CONFIRMED);

        if (updateReqVO.getPerformance() != null) monthly.setPerformance(updateReqVO.getPerformance());
        if (updateReqVO.getCommission() != null)  monthly.setCommission(updateReqVO.getCommission());
        if (updateReqVO.getRemark() != null)       monthly.setRemark(updateReqVO.getRemark());

        BigDecimal total = monthly.getBaseSalary()
                .add(monthly.getPositionSalary())
                .add(monthly.getPerformance())
                .add(monthly.getCommission())
                .add(monthly.getAllowance())
                .add(monthly.getFullAttendanceBonus())
                .subtract(monthly.getAttendanceDeduction() != null ? monthly.getAttendanceDeduction() : BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
        monthly.setTotalSalary(total);
        monthlyMapper.updateById(monthly);
    }

    // ===== 薪资确认（改为发通知单流程）=====

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmSalaryMonthly(Long id) {
        SalaryMonthlyDO monthly = monthlyMapper.selectById(id);
        if (monthly == null) throw exception(HR_SALARY_MONTHLY_NOT_FOUND);
        if (monthly.getStatus() >= 1) throw exception(HR_SALARY_MONTHLY_CONFIRMED);

        // 检查是否已发送通知（未确认的通知单已存在则不重复发）
        if (monthly.getConfirmNoticeId() != null && monthly.getConfirmNoticeId() > 0) {
            SalaryConfirmNoticeDO existNotice = confirmNoticeMapper.selectById(monthly.getConfirmNoticeId());
            if (existNotice != null && existNotice.getStatus() == 0) {
                throw exception(HR_SALARY_CONFIRM_NOTICE_ALREADY_SENT);
            }
        }

        // 构建通知内容（将当前明细快照存入通知单）
        String noticeContent = monthly.getSalaryDetail() != null ? monthly.getSalaryDetail() : "{}";

        // 创建薪资确认通知单
        SalaryConfirmNoticeDO notice = new SalaryConfirmNoticeDO();
        notice.setSalaryMonthlyId(id);
        notice.setUserId(monthly.getUserId());
        notice.setYearMonth(monthly.getYearMonth());
        notice.setTotalSalary(monthly.getTotalSalary());
        notice.setNoticeContent(noticeContent);
        notice.setSendTime(LocalDateTime.now());
        notice.setStatus(0); // 待确认
        notice.setRemark("");
        confirmNoticeMapper.insert(notice);

        // 记录通知单ID到薪资单
        monthly.setConfirmNoticeId(notice.getId());
        monthlyMapper.updateById(monthly);

        log.info("[confirmSalaryMonthly] 薪资单ID={} 已发送确认通知单ID={} 给用户={}", id, notice.getId(), monthly.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchConfirmSalaryMonthly(List<Long> ids) {
        ids.forEach(this::confirmSalaryMonthly);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void employeeConfirmSalary(Long noticeId, Long userId) {
        SalaryConfirmNoticeDO notice = confirmNoticeMapper.selectById(noticeId);
        if (notice == null) throw exception(HR_SALARY_CONFIRM_NOTICE_NOT_FOUND);
        if (!notice.getUserId().equals(userId)) throw exception(HR_SALARY_CONFIRM_NOTICE_NOT_FOUND);
        if (notice.getStatus() == 1) throw exception(HR_SALARY_CONFIRM_NOTICE_ALREADY_CONFIRMED);

        // 更新通知单为已确认
        notice.setStatus(1);
        notice.setConfirmTime(LocalDateTime.now());
        confirmNoticeMapper.updateById(notice);

        // 同步更新月度薪资状态为已确认
        SalaryMonthlyDO monthly = monthlyMapper.selectById(notice.getSalaryMonthlyId());
        if (monthly != null) {
            monthly.setStatus(1);
            monthlyMapper.updateById(monthly);
        }
        log.info("[employeeConfirmSalary] 通知单ID={} 用户={} 已确认薪资", noticeId, userId);
    }

    @Override
    public List<SalaryConfirmNoticeDO> getMyPendingConfirmNotices(Long userId) {
        return confirmNoticeMapper.selectPendingByUserId(userId);
    }

    @Override
    public SalaryMonthlyRespVO getMyLatestSalary(Long userId) {
        SalaryMonthlyDO monthly = monthlyMapper.selectLatestByUser(userId);
        return BeanUtils.toBean(monthly, SalaryMonthlyRespVO.class);
    }

    @Override
    public List<SalaryMonthlyRespVO> getMySalaryList(Long userId) {
        List<SalaryMonthlyDO> list = monthlyMapper.selectListByUserConfirmed(userId);
        return BeanUtils.toBean(list, SalaryMonthlyRespVO.class);
    }

}
