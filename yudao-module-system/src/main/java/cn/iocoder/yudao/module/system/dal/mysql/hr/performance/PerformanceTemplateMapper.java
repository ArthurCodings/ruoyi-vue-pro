package cn.iocoder.yudao.module.system.dal.mysql.hr.performance;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.PerformanceTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PerformanceTemplateMapper extends BaseMapperX<PerformanceTemplateDO> {

    default List<PerformanceTemplateDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<PerformanceTemplateDO>()
                .eqIfPresent(PerformanceTemplateDO::getStatus, status)
                .orderByAsc(PerformanceTemplateDO::getId));
    }

}
