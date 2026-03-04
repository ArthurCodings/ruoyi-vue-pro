package cn.iocoder.yudao.module.system.dal.mysql.hr.performance;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.PerformanceTemplateUserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PerformanceTemplateUserMapper extends BaseMapperX<PerformanceTemplateUserDO> {

    default List<PerformanceTemplateUserDO> selectListByTemplateId(Long templateId) {
        return selectList(PerformanceTemplateUserDO::getTemplateId, templateId);
    }

    default PerformanceTemplateUserDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<PerformanceTemplateUserDO>()
                .eq(PerformanceTemplateUserDO::getUserId, userId)
                .last("LIMIT 1"));
    }

    default void deleteByTemplateId(Long templateId) {
        delete(new LambdaQueryWrapperX<PerformanceTemplateUserDO>()
                .eq(PerformanceTemplateUserDO::getTemplateId, templateId));
    }

    default void deleteByTemplateIdAndUserId(Long templateId, Long userId) {
        delete(new LambdaQueryWrapperX<PerformanceTemplateUserDO>()
                .eq(PerformanceTemplateUserDO::getTemplateId, templateId)
                .eq(PerformanceTemplateUserDO::getUserId, userId));
    }

}
