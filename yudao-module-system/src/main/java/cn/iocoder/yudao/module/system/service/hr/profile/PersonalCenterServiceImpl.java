package cn.iocoder.yudao.module.system.service.hr.profile;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.api.contract.ContractApi;
import cn.iocoder.yudao.module.infra.api.contract.dto.ContractSimpleDTO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.record.AttendanceMonthlySummaryVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.profile.vo.PersonalCenterPanelVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRecordDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryConfigDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryMonthlyDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceRecordMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.employee.HrEmployeeMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.SalaryConfigMapper;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.SalaryMonthlyMapper;
import cn.iocoder.yudao.module.system.service.hr.attendance.AttendanceRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

@Service
@Validated
@Slf4j
public class PersonalCenterServiceImpl implements PersonalCenterService {

    @Resource
    private HrEmployeeMapper employeeMapper;
    @Resource
    private AttendanceRecordService attendanceRecordService;
    @Resource
    private AttendanceRecordMapper recordMapper;
    @Resource
    private SalaryMonthlyMapper monthlyMapper;
    @Resource
    private SalaryConfigMapper salaryConfigMapper;
    @Resource
    private ContractApi contractApi;

    @Override
    public PersonalCenterPanelVO getPanel(Long userId, Integer yearMonth) {
        if (yearMonth == null) {
            YearMonth ym = YearMonth.now();
            yearMonth = ym.getYear() * 100 + ym.getMonthValue();
        }

        // 1. 员工档案
        HrEmployeeRespVO employee = null;
        HrEmployeeDO emp = employeeMapper.selectByUserId(userId);
        if (emp != null) {
            employee = BeanUtils.toBean(emp, HrEmployeeRespVO.class);
            // 个人中心不脱敏身份证
        }

        // 2. 本月考勤汇总
        AttendanceMonthlySummaryVO attendance = null;
        try {
            attendance = attendanceRecordService.getMyMonthlySummary(userId, yearMonth);
        } catch (Exception e) {
            log.warn("[PersonalCenterPanel] 获取考勤汇总失败 userId={}", userId, e);
        }

        // 3. 今日打卡状态
        boolean todayClockIn = false;
        boolean todayClockOut = false;
        AttendanceRecordDO todayRecord = recordMapper.selectByUserAndDate(userId, LocalDate.now());
        if (todayRecord != null) {
            todayClockIn = todayRecord.getClockInTime() != null;
            todayClockOut = todayRecord.getClockOutTime() != null;
        }

        // 4. 最新薪资单（已确认/已发放）
        SalaryMonthlyRespVO latestSalary = null;
        SalaryMonthlyDO monthly = monthlyMapper.selectLatestByUser(userId);
        if (monthly != null) {
            latestSalary = BeanUtils.toBean(monthly, SalaryMonthlyRespVO.class);
        }

        // 5. 薪资公式配置
        SalaryConfigRespVO salaryConfig = null;
        SalaryConfigDO config = salaryConfigMapper.selectFirst();
        if (config != null) {
            salaryConfig = BeanUtils.toBean(config, SalaryConfigRespVO.class);
        }

        // 6. 负责的合同列表（跨模块调用 infra）
        List<ContractSimpleDTO> contracts = Collections.emptyList();
        try {
            contracts = contractApi.getContractListByUserId(userId);
        } catch (Exception e) {
            log.warn("[PersonalCenterPanel] 获取合同列表失败 userId={}", userId, e);
        }

        return PersonalCenterPanelVO.builder()
                .employee(employee)
                .attendance(attendance)
                .todayClockIn(todayClockIn)
                .todayClockOut(todayClockOut)
                .latestSalary(latestSalary)
                .salaryConfig(salaryConfig)
                .contracts(contracts)
                .currentYearMonth(yearMonth)
                .build();
    }

}
