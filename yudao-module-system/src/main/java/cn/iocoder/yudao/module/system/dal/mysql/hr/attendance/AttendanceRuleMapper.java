package cn.iocoder.yudao.module.system.dal.mysql.hr.attendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.hr.attendance.vo.rule.AttendanceRulePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance.AttendanceRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttendanceRuleMapper extends BaseMapperX<AttendanceRuleDO> {

    default PageResult<AttendanceRuleDO> selectPage(AttendanceRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AttendanceRuleDO>()
                .likeIfPresent(AttendanceRuleDO::getName, reqVO.getName())
                .eqIfPresent(AttendanceRuleDO::getShiftType, reqVO.getShiftType())
                .eqIfPresent(AttendanceRuleDO::getStatus, reqVO.getStatus())
                .orderByDesc(AttendanceRuleDO::getId));
    }

    default List<AttendanceRuleDO> selectListByStatus(Integer status) {
        return selectList(AttendanceRuleDO::getStatus, status);
    }

    default AttendanceRuleDO selectDefaultLateShift() {
        return selectOne(new LambdaQueryWrapperX<AttendanceRuleDO>()
                .eq(AttendanceRuleDO::getShiftType, 2)
                .eq(AttendanceRuleDO::getStatus, 0)
                .last("LIMIT 1"));
    }

}
