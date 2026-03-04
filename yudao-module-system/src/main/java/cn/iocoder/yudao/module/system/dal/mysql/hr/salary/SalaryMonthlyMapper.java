package cn.iocoder.yudao.module.system.dal.mysql.hr.salary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.monthly.SalaryMonthlyPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryMonthlyDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    default void deleteByUserAndMonth(Long userId, Integer yearMonth) {
        delete(new LambdaQueryWrapperX<SalaryMonthlyDO>()
                .eq(SalaryMonthlyDO::getUserId, userId)
                .eq(SalaryMonthlyDO::getYearMonth, yearMonth));
    }

    /**
     * 物理删除指定用户、月份的薪资记录（用于重算前清理，避免逻辑删除导致 uk_user_month 唯一键冲突）
     */
    @Delete("DELETE FROM hr_salary_monthly WHERE user_id = #{userId} AND `year_month` = #{yearMonth}")
    void physicalDeleteByUserAndMonth(@Param("userId") Long userId, @Param("yearMonth") Integer yearMonth);

}
