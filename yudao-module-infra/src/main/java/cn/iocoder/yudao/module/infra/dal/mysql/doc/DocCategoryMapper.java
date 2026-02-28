package cn.iocoder.yudao.module.infra.dal.mysql.doc;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.doc.DocCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocCategoryMapper extends BaseMapperX<DocCategoryDO> {

    default List<DocCategoryDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<DocCategoryDO>()
                .eqIfPresent(DocCategoryDO::getStatus, status)
                .orderByAsc(DocCategoryDO::getSort));
    }

    default List<DocCategoryDO> selectAll() {
        return selectList(new LambdaQueryWrapperX<DocCategoryDO>()
                .orderByAsc(DocCategoryDO::getSort));
    }

}
