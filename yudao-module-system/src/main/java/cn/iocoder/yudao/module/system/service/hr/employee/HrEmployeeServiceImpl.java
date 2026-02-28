package cn.iocoder.yudao.module.system.service.hr.employee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.employee.HrEmployeeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.HR_EMPLOYEE_NOT_FOUND;

@Service
@Validated
public class HrEmployeeServiceImpl implements HrEmployeeService {

    @Resource
    private HrEmployeeMapper employeeMapper;

    @Override
    public Long createEmployee(HrEmployeeSaveReqVO createReqVO) {
        HrEmployeeDO emp = BeanUtils.toBean(createReqVO, HrEmployeeDO.class);
        employeeMapper.insert(emp);
        return emp.getId();
    }

    @Override
    public void updateEmployee(HrEmployeeSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        HrEmployeeDO updateObj = BeanUtils.toBean(updateReqVO, HrEmployeeDO.class);
        employeeMapper.updateById(updateObj);
    }

    @Override
    public void deleteEmployee(Long id) {
        validateExists(id);
        employeeMapper.deleteById(id);
    }

    @Override
    public HrEmployeeDO getEmployee(Long id) {
        return employeeMapper.selectById(id);
    }

    @Override
    public PageResult<HrEmployeeDO> getEmployeePage(HrEmployeePageReqVO reqVO) {
        return employeeMapper.selectPage(reqVO);
    }

    @Override
    public HrEmployeeDO getEmployeeByUserId(Long userId) {
        return employeeMapper.selectByUserId(userId);
    }

    @Override
    public HrEmployeeRespVO getMyProfile(Long userId) {
        HrEmployeeDO emp = employeeMapper.selectByUserId(userId);
        if (emp == null) {
            throw exception(HR_EMPLOYEE_NOT_FOUND);
        }
        return BeanUtils.toBean(emp, HrEmployeeRespVO.class);
    }

    @Override
    public void createFromSystemUser(Long userId, String nickname, Long deptId, String deptName,
                                     Long postId, String postName, String mobile, String email, Integer sex) {
        if (employeeMapper.selectByUserId(userId) != null) {
            return;
        }
        HrEmployeeDO emp = new HrEmployeeDO();
        emp.setUserId(userId);
        emp.setNickname(nickname);
        emp.setDeptId(deptId != null ? deptId : 0L);
        emp.setDeptName(deptName != null ? deptName : "");
        emp.setPostId(postId != null ? postId : 0L);
        emp.setPostName(postName != null ? postName : "");
        emp.setMobile(mobile != null ? mobile : "");
        emp.setEmail(email != null ? email : "");
        emp.setSex(sex != null ? sex : 0);
        emp.setAvatar("");
        emp.setEmploymentType(1);
        emp.setEmploymentStatus(3); // 待入职
        emp.setDefaultRuleId(0L);
        emp.setIdCard("");
        emp.setEmergencyContact("");
        emp.setEmergencyPhone("");
        emp.setRemark("");
        employeeMapper.insert(emp);
    }

    private void validateExists(Long id) {
        if (id != null && employeeMapper.selectById(id) == null) {
            throw exception(HR_EMPLOYEE_NOT_FOUND);
        }
    }

}
