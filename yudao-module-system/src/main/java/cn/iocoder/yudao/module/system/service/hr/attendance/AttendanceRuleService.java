package cn.iocoder.yudao.module.system.service.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule.AttendanceRulePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule.AttendanceRuleSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRuleDO;

import java.util.List;

public interface AttendanceRuleService {

    Long createAttendanceRule(AttendanceRuleSaveReqVO createReqVO);

    void updateAttendanceRule(AttendanceRuleSaveReqVO updateReqVO);

    void deleteAttendanceRule(Long id);

    AttendanceRuleDO getAttendanceRule(Long id);

    PageResult<AttendanceRuleDO> getAttendanceRulePage(AttendanceRulePageReqVO reqVO);

    List<AttendanceRuleDO> getEnabledRuleList();

    /** 获取系统默认晚班规则（用于排班生成兜底） */
    AttendanceRuleDO getDefaultLateShiftRule();

}
