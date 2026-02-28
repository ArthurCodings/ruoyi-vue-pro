package cn.iocoder.yudao.module.infra.dal.mysql.doc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.InfraDocPageReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.doc.InfraDocDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InfraDocMapper extends BaseMapperX<InfraDocDO> {

    default PageResult<InfraDocDO> selectPage(InfraDocPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InfraDocDO>()
                .eqIfPresent(InfraDocDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(InfraDocDO::getType, reqVO.getType())
                .eqIfPresent(InfraDocDO::getStatus, reqVO.getStatus())
                .likeIfPresent(InfraDocDO::getName, reqVO.getName())
                .orderByAsc(InfraDocDO::getSort));
    }

}
