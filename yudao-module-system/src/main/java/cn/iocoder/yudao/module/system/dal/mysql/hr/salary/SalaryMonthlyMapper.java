package cn.iocoder.yudao.module.system.dal.mysql.hr.salary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryMonthlyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalaryMonthlyMapper extends BaseMapperX<SalaryMonthlyDO> {

    default PageResult<SalaryMonthlyDO> selectPage(SalaryMonthlyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SalaryMonthlyDO>()
                .eqIfPresent(SalaryMonthlyDO::getUserId, reqVO.getUserId())
                .eqIfPresent(SalaryMonthlyDO::getYearMonth, reqVO.getYearMonth())
                .eqIfPresent(SalaryMonthlyDO::getStatus, reqVO.getStatus())
                .orderByDesc(SalaryMonthlyDO::getYearMonth));
    }

    default SalaryMonthlyDO selectByUserAndMonth(Long userId, Integer yearMonth) {
        return selectOne(new LambdaQueryWrapperX<SalaryMonthlyDO>()
                .eq(SalaryMonthlyDO::getUserId, userId)
                .eq(SalaryMonthlyDO::getYearMonth, yearMonth));
    }

    default SalaryMonthlyDO selectLatestByUser(Long userId) {
        return selectOne(new LambdaQueryWrapperX<SalaryMonthlyDO>()
                .eq(SalaryMonthlyDO::getUserId, userId)
                .in(SalaryMonthlyDO::getStatus, List.of(1, 2))
                .orderByDesc(SalaryMonthlyDO::getYearMonth)
                .last("LIMIT 1"));
    }

    default List<SalaryMonthlyDO> selectListByUserConfirmed(Long userId) {
        return selectList(new LambdaQueryWrapperX<SalaryMonthlyDO>()
                .eq(SalaryMonthlyDO::getUserId, userId)
                .in(SalaryMonthlyDO::getStatus, List.of(1, 2))
                .orderByDesc(SalaryMonthlyDO::getYearMonth));
    }

    default List<SalaryMonthlyDO> selectListByMonth(Integer yearMonth) {
        return selectList(SalaryMonthlyDO::getYearMonth, yearMonth);
    }

}
