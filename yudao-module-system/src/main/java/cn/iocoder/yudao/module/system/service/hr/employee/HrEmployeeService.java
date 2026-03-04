package cn.iocoder.yudao.module.system.service.hr.employee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;

import java.util.Collection;
import java.util.Map;

public interface HrEmployeeService {

    Long createEmployee(HrEmployeeSaveReqVO createReqVO);

    void updateEmployee(HrEmployeeSaveReqVO updateReqVO);

    void deleteEmployee(Long id);

    HrEmployeeDO getEmployee(Long id);

    PageResult<HrEmployeeDO> getEmployeePage(HrEmployeePageReqVO reqVO);

    /** 根据系统用户ID查花名册 */
    HrEmployeeDO getEmployeeByUserId(Long userId);

    /** 个人中心：查本人员工档案（含完整敏感字段） */
    HrEmployeeRespVO getMyProfile(Long userId);

    /** 系统创建用户后，自动同步创建花名册记录（待入职状态） */
    void createFromSystemUser(Long userId, String nickname, Long deptId, String deptName, Long postId, String postName, String mobile, String email, Integer sex);

    /**
     * 根据用户ID列表批量获取员工姓名映射（用于列表展示）
     *
     * @param userIds 用户ID集合
     * @return userId -> nickname 映射，无花名册记录时 nickname 为空字符串
     */
    Map<Long, String> getNicknameMap(Collection<Long> userIds);

    /**
     * 根据用户ID列表批量获取花名册记录（用于联查部门、岗位等）
     *
     * @param userIds 用户ID集合
     * @return userId -> HrEmployeeDO 映射
     */
    Map<Long, HrEmployeeDO> getEmployeeMap(Collection<Long> userIds);

}
