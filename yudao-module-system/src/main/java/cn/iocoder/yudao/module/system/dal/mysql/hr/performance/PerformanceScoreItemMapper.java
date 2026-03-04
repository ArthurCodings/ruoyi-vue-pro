package cn.iocoder.yudao.module.system.dal.mysql.hr.performance;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.PerformanceScoreItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PerformanceScoreItemMapper extends BaseMapperX<PerformanceScoreItemDO> {

    default List<PerformanceScoreItemDO> selectListByScoreId(Long scoreId) {
        return selectList(PerformanceScoreItemDO::getScoreId, scoreId);
    }

    default void deleteByScoreId(Long scoreId) {
        delete(new LambdaQueryWrapperX<PerformanceScoreItemDO>()
                .eq(PerformanceScoreItemDO::getScoreId, scoreId));
    }

}
