package cn.iocoder.yudao.module.system.dal.mysql.hr.performance;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.PerformanceTemplateItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PerformanceTemplateItemMapper extends BaseMapperX<PerformanceTemplateItemDO> {

    default List<PerformanceTemplateItemDO> selectListBySectionId(Long sectionId) {
        return selectList(new LambdaQueryWrapperX<PerformanceTemplateItemDO>()
                .eq(PerformanceTemplateItemDO::getSectionId, sectionId)
                .orderByAsc(PerformanceTemplateItemDO::getSort));
    }

    default List<PerformanceTemplateItemDO> selectListByTemplateId(Long templateId) {
        return selectList(new LambdaQueryWrapperX<PerformanceTemplateItemDO>()
                .eq(PerformanceTemplateItemDO::getTemplateId, templateId)
                .orderByAsc(PerformanceTemplateItemDO::getSort));
    }

    default void deleteByTemplateId(Long templateId) {
        delete(new LambdaQueryWrapperX<PerformanceTemplateItemDO>()
                .eq(PerformanceTemplateItemDO::getTemplateId, templateId));
    }

}
