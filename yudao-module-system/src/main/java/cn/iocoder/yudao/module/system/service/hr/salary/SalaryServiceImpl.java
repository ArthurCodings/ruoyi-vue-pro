package cn.iocoder.yudao.module.system.service.hr.salary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalaryPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalarySaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRecordDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.EmployeeSalaryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryConfigDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryMonthlyDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceRecordMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceScheduleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.employee.HrEmployeeMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.EmployeeSalaryMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.SalaryConfigMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.SalaryMonthlyMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class SalaryServiceImpl implements SalaryService {

    @Resource
    private SalaryConfigMapper salaryConfigMapper;
    @Resource
    private EmployeeSalaryMapper employeeSalaryMapper;
    @Resource
    private SalaryMonthlyMapper monthlyMapper;
    @Resource
    private HrEmployeeMapper employeeMapper;
    @Resource
    private AttendanceScheduleMapper scheduleMapper;
    @Resource
    private AttendanceRecordMapper recordMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public void generateMonthly(Integer yearMonth) {
        SalaryConfigDO config = salaryConfigMapper.selectFirst();
        if (config == null) {
            throw exception(HR_SALARY_CONFIG_NOT_FOUND);
        }
        List<HrEmployeeDO> employees = employeeMapper.selectListByStatus(1);
        for (HrEmployeeDO emp : employees) {
            if (monthlyMapper.selectByUserAndMonth(emp.getUserId(), yearMonth) != null) {
                continue; // 已存在，跳过
            }
            EmployeeSalaryDO salaryRecord = employeeSalaryMapper.selectByUserId(emp.getUserId());

            SalaryMonthlyDO monthly = new SalaryMonthlyDO();
            monthly.setUserId(emp.getUserId());
            monthly.setYearMonth(yearMonth);
            monthly.setBaseSalary(salaryRecord != null ? salaryRecord.getBaseSalary() : BigDecimal.ZERO);
            monthly.setPositionSalary(salaryRecord != null ? salaryRecord.getPositionSalary() : BigDecimal.ZERO);
            monthly.setAllowance(salaryRecord != null ? salaryRecord.getAllowance() : BigDecimal.ZERO);
            monthly.setPerformance(BigDecimal.ZERO); // 等待手动填写
            monthly.setCommission(BigDecimal.ZERO);  // 等待手动填写或从合同取数

            // 计算考勤数据
            int shouldAttend = scheduleMapper.countNeedClockByUserAndMonth(emp.getUserId(), yearMonth);
            int actualAttend = recordMapper.countByUserMonthAndStatusIn(emp.getUserId(), yearMonth, List.of(0, 1));
            int lateCount = recordMapper.countByUserMonthAndStatusIn(emp.getUserId(), yearMonth, List.of(1));
            int absentCount = recordMapper.countByUserMonthAndStatusIn(emp.getUserId(), yearMonth, List.of(3));

            monthly.setShouldAttendDays(shouldAttend);
            monthly.setActualAttendDays(actualAttend);
            monthly.setLateCount(lateCount);
            monthly.setAbsentCount(absentCount);

            // 全勤奖
            BigDecimal fullBonus = (absentCount == 0 && lateCount == 0 && Boolean.TRUE.equals(config.getEnableFullAttendance()))
                    ? config.getFullAttendanceAmount()
                    : BigDecimal.ZERO;
            monthly.setFullAttendanceBonus(fullBonus);

            // 合计
            BigDecimal total = monthly.getBaseSalary()
                    .add(monthly.getPositionSalary())
                    .add(monthly.getPerformance())
                    .add(monthly.getCommission())
                    .add(monthly.getAllowance())
                    .add(monthly.getFullAttendanceBonus());
            monthly.setTotalSalary(total);
            monthly.setStatus(0); // 待确认
            monthly.setRemark("");

            monthlyMapper.insert(monthly);
        }
        log.info("[generateMonthly] 年月={} 共生成 {} 条薪资草稿", yearMonth, employees.size());
    }

    @Override
    public void updateSalaryMonthly(SalaryMonthlyUpdateReqVO updateReqVO) {
        SalaryMonthlyDO monthly = monthlyMapper.selectById(updateReqVO.getId());
        if (monthly == null) throw exception(HR_SALARY_MONTHLY_NOT_FOUND);
        if (monthly.getStatus() >= 1) throw exception(HR_SALARY_MONTHLY_CONFIRMED);

        if (updateReqVO.getPerformance() != null) monthly.setPerformance(updateReqVO.getPerformance());
        if (updateReqVO.getCommission() != null) monthly.setCommission(updateReqVO.getCommission());
        if (updateReqVO.getRemark() != null) monthly.setRemark(updateReqVO.getRemark());

        BigDecimal total = monthly.getBaseSalary()
                .add(monthly.getPositionSalary())
                .add(monthly.getPerformance())
                .add(monthly.getCommission())
                .add(monthly.getAllowance())
                .add(monthly.getFullAttendanceBonus());
        monthly.setTotalSalary(total);
        monthlyMapper.updateById(monthly);
    }

    @Override
    public void confirmSalaryMonthly(Long id) {
        SalaryMonthlyDO monthly = monthlyMapper.selectById(id);
        if (monthly == null) throw exception(HR_SALARY_MONTHLY_NOT_FOUND);
        monthly.setStatus(1);
        monthlyMapper.updateById(monthly);
    }

    @Override
    public void batchConfirmSalaryMonthly(List<Long> ids) {
        ids.forEach(this::confirmSalaryMonthly);
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
