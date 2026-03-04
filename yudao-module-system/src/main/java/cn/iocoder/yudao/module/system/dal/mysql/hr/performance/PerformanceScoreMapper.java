package cn.iocoder.yudao.module.system.dal.mysql.hr.performance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.PerformanceScoreDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PerformanceScoreMapper extends BaseMapperX<PerformanceScoreDO> {

    default PerformanceScoreDO selectByUserAndMonth(Long userId, Integer yearMonth) {
        return selectOne(new LambdaQueryWrapperX<PerformanceScoreDO>()
                .eq(PerformanceScoreDO::getUserId, userId)
                .eq(PerformanceScoreDO::getYearMonth, yearMonth));
    }

    default List<PerformanceScoreDO> selectListByMonth(Integer yearMonth) {
        return selectList(new LambdaQueryWrapperX<PerformanceScoreDO>()
                .eq(PerformanceScoreDO::getYearMonth, yearMonth)
                .orderByAsc(PerformanceScoreDO::getUserId));
    }

    default PageResult<PerformanceScoreDO> selectPage(Integer yearMonth, Long userId, Integer status,
                                                       cn.iocoder.yudao.framework.common.pojo.PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<PerformanceScoreDO>()
                .eqIfPresent(PerformanceScoreDO::getYearMonth, yearMonth)
                .eqIfPresent(PerformanceScoreDO::getUserId, userId)
                .eqIfPresent(PerformanceScoreDO::getStatus, status)
                .orderByDesc(PerformanceScoreDO::getYearMonth));
    }

}
