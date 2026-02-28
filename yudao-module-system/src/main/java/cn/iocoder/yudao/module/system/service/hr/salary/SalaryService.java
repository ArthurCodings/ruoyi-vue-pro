package cn.iocoder.yudao.module.system.service.hr.salary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.config.SalaryConfigSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalaryPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalarySaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.EmployeeSalaryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryMonthlyDO;

import java.util.List;

public interface SalaryService {

    // ---- 薪资配置 ----
    SalaryConfigRespVO getSalaryConfig();
    void saveSalaryConfig(SalaryConfigSaveReqVO saveReqVO);

    // ---- 员工薪资档案 ----
    Long createEmployeeSalary(EmployeeSalarySaveReqVO createReqVO);
    void updateEmployeeSalary(EmployeeSalarySaveReqVO updateReqVO);
    EmployeeSalaryDO getEmployeeSalary(Long id);
    PageResult<EmployeeSalaryDO> getEmployeeSalaryPage(EmployeeSalaryPageReqVO reqVO);
    EmployeeSalaryDO getEmployeeSalaryByUserId(Long userId);

    // ---- 月度薪资 ----
    PageResult<SalaryMonthlyDO> getSalaryMonthlyPage(SalaryMonthlyPageReqVO reqVO);

    /** 发起月结算（生成草稿） */
    void generateMonthly(Integer yearMonth);

    /** 手动编辑绩效/提成 */
    void updateSalaryMonthly(SalaryMonthlyUpdateReqVO updateReqVO);

    /** 确认薪资单 */
    void confirmSalaryMonthly(Long id);

    /** 批量确认 */
    void batchConfirmSalaryMonthly(List<Long> ids);

    /** 个人中心：最新薪资单 */
    SalaryMonthlyRespVO getMyLatestSalary(Long userId);

    /** 个人中心：历史薪资列表 */
    List<SalaryMonthlyRespVO> getMySalaryList(Long userId);

}
