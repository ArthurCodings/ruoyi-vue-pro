package cn.iocoder.yudao.module.system.dal.mysql.hr.employee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo.HrEmployeePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.employee.HrEmployeeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mapper
public interface HrEmployeeMapper extends BaseMapperX<HrEmployeeDO> {

    default PageResult<HrEmployeeDO> selectPage(HrEmployeePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HrEmployeeDO>()
                .likeIfPresent(HrEmployeeDO::getNickname, reqVO.getNickname())
                .eqIfPresent(HrEmployeeDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(HrEmployeeDO::getEmploymentType, reqVO.getEmploymentType())
                .eqIfPresent(HrEmployeeDO::getEmploymentStatus, reqVO.getEmploymentStatus())
                .orderByDesc(HrEmployeeDO::getId));
    }

    default HrEmployeeDO selectByUserId(Long userId) {
        return selectOne(HrEmployeeDO::getUserId, userId);
    }

    default List<HrEmployeeDO> selectListByStatus(Integer employmentStatus) {
        return selectList(HrEmployeeDO::getEmploymentStatus, employmentStatus);
    }

    /** 根据用户ID列表批量查询花名册（用于填充员工姓名） */
    default List<HrEmployeeDO> selectListByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(HrEmployeeDO::getUserId, userIds);
    }

}
