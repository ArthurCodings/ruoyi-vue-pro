package cn.iocoder.yudao.module.system.service.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule.AttendanceRulePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule.AttendanceRuleSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRuleDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.attendance.AttendanceRuleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AttendanceRuleServiceImpl implements AttendanceRuleService {

    @Resource
    private AttendanceRuleMapper attendanceRuleMapper;

    @Override
    public Long createAttendanceRule(AttendanceRuleSaveReqVO createReqVO) {
        AttendanceRuleDO rule = BeanUtils.toBean(createReqVO, AttendanceRuleDO.class);
        attendanceRuleMapper.insert(rule);
        return rule.getId();
    }

    @Override
    public void updateAttendanceRule(AttendanceRuleSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        AttendanceRuleDO updateObj = BeanUtils.toBean(updateReqVO, AttendanceRuleDO.class);
        attendanceRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteAttendanceRule(Long id) {
        validateExists(id);
        attendanceRuleMapper.deleteById(id);
    }

    @Override
    public AttendanceRuleDO getAttendanceRule(Long id) {
        return attendanceRuleMapper.selectById(id);
    }

    @Override
    public PageResult<AttendanceRuleDO> getAttendanceRulePage(AttendanceRulePageReqVO reqVO) {
        return attendanceRuleMapper.selectPage(reqVO);
    }

    @Override
    public List<AttendanceRuleDO> getEnabledRuleList() {
        return attendanceRuleMapper.selectListByStatus(0);
    }

    @Override
    public AttendanceRuleDO getDefaultLateShiftRule() {
        AttendanceRuleDO rule = attendanceRuleMapper.selectDefaultLateShift();
        if (rule == null) {
            throw exception(HR_ATTENDANCE_RULE_NOT_FOUND);
        }
        return rule;
    }

    private void validateExists(Long id) {
        if (id != null && attendanceRuleMapper.selectById(id) == null) {
            throw exception(HR_ATTENDANCE_RULE_NOT_FOUND);
        }
    }

}
