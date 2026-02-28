package cn.iocoder.yudao.module.system.dal.mysql.hr.salary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.employeesalary.EmployeeSalaryPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.EmployeeSalaryDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeSalaryMapper extends BaseMapperX<EmployeeSalaryDO> {

    default PageResult<EmployeeSalaryDO> selectPage(EmployeeSalaryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeSalaryDO>()
                .eqIfPresent(EmployeeSalaryDO::getUserId, reqVO.getUserId())
                .orderByDesc(EmployeeSalaryDO::getEffectiveDate));
    }

    default EmployeeSalaryDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<EmployeeSalaryDO>()
                .eq(EmployeeSalaryDO::getUserId, userId)
                .orderByDesc(EmployeeSalaryDO::getEffectiveDate)
                .last("LIMIT 1"));
    }

}
