package cn.iocoder.yudao.module.system.service.hr.employee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeeSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.PostDO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.employee.HrEmployeeMapper;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.dept.PostService;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.HR_EMPLOYEE_NOT_FOUND;

@Service
@Validated
public class HrEmployeeServiceImpl implements HrEmployeeService {

    @Resource
    private HrEmployeeMapper employeeMapper;
    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;

    @Override
    public Long createEmployee(HrEmployeeSaveReqVO createReqVO) {
        HrEmployeeDO emp = BeanUtils.toBean(createReqVO, HrEmployeeDO.class);
        fillDeptAndPostName(emp);
        employeeMapper.insert(emp);
        return emp.getId();
    }

    @Override
    public void updateEmployee(HrEmployeeSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        HrEmployeeDO updateObj = BeanUtils.toBean(updateReqVO, HrEmployeeDO.class);
        fillDeptAndPostName(updateObj);
        employeeMapper.updateById(updateObj);
    }

    /**
     * 根据 deptId、postId 填充部门名称、岗位名称快照。
     * 快照意义：保存当时的名称，即使后续部门/岗位被重命名或删除，历史记录仍可正确展示。
     */
    private void fillDeptAndPostName(HrEmployeeDO emp) {
        if (emp.getDeptId() != null && emp.getDeptId() > 0) {
            DeptDO dept = deptService.getDept(emp.getDeptId());
            emp.setDeptName(dept != null ? dept.getName() : "");
        } else {
            emp.setDeptName("");
        }
        if (emp.getPostId() != null && emp.getPostId() > 0) {
            PostDO post = postService.getPost(emp.getPostId());
            emp.setPostName(post != null ? post.getName() : "");
        } else {
            emp.setPostName("");
        }
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

    @Override
    public Map<Long, String> getNicknameMap(Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<HrEmployeeDO> list = employeeMapper.selectListByUserIds(userIds);
        return list.stream().collect(Collectors.toMap(HrEmployeeDO::getUserId, e -> e.getNickname() != null ? e.getNickname() : "", (a, b) -> a));
    }

    @Override
    public Map<Long, HrEmployeeDO> getEmployeeMap(Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<HrEmployeeDO> list = employeeMapper.selectListByUserIds(userIds);
        return list.stream().collect(Collectors.toMap(HrEmployeeDO::getUserId, e -> e, (a, b) -> a));
    }

    private void validateExists(Long id) {
        if (id != null && employeeMapper.selectById(id) == null) {
            throw exception(HR_EMPLOYEE_NOT_FOUND);
        }
    }

}
