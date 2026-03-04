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
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryConfirmNoticeDO;
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

    /** 根据ID获取月度薪资详情（含计算明细JSON） */
    SalaryMonthlyDO getSalaryMonthlyById(Long id);

    /** 发起月结算（生成草稿，已有则重置重算） */
    void generateMonthly(Integer yearMonth);

    /** 手动编辑绩效/提成 */
    void updateSalaryMonthly(SalaryMonthlyUpdateReqVO updateReqVO);

    /**
     * 确认薪资单：向员工发送薪资确认通知单，等待员工确认
     * 不直接修改薪资单状态，需员工在个人中心点击确认后更新
     */
    void confirmSalaryMonthly(Long id);

    /** 批量确认（批量发送通知） */
    void batchConfirmSalaryMonthly(List<Long> ids);

    /**
     * 员工确认薪资：在个人中心点击确认后调用
     * 更新通知单状态为已确认，并同步月度薪资状态为已确认
     */
    void employeeConfirmSalary(Long noticeId, Long userId);

    /** 个人中心：查询待确认的薪资通知单列表 */
    List<SalaryConfirmNoticeDO> getMyPendingConfirmNotices(Long userId);

    /** 个人中心：最新薪资单 */
    SalaryMonthlyRespVO getMyLatestSalary(Long userId);

    /** 个人中心：历史薪资列表 */
    List<SalaryMonthlyRespVO> getMySalaryList(Long userId);

}
