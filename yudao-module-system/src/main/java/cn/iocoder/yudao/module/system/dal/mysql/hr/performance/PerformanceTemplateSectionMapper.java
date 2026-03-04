package cn.iocoder.yudao.module.system.dal.mysql.hr.performance;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.PerformanceTemplateSectionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PerformanceTemplateSectionMapper extends BaseMapperX<PerformanceTemplateSectionDO> {

    default List<PerformanceTemplateSectionDO> selectListByTemplateId(Long templateId) {
        return selectList(new LambdaQueryWrapperX<PerformanceTemplateSectionDO>()
                .eq(PerformanceTemplateSectionDO::getTemplateId, templateId)
                .orderByAsc(PerformanceTemplateSectionDO::getSort));
    }

    default void deleteByTemplateId(Long templateId) {
        delete(new LambdaQueryWrapperX<PerformanceTemplateSectionDO>()
                .eq(PerformanceTemplateSectionDO::getTemplateId, templateId));
    }

}
