package cn.iocoder.yudao.module.infra.dal.mysql.contract;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractPageReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.contract.InfraContractDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface InfraContractMapper extends BaseMapperX<InfraContractDO> {

    default PageResult<InfraContractDO> selectPage(InfraContractPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InfraContractDO>()
                .likeIfPresent(InfraContractDO::getContractName, reqVO.getContractName())
                .eqIfPresent(InfraContractDO::getType, reqVO.getType())
                .eqIfPresent(InfraContractDO::getStatus, reqVO.getStatus())
                .eqIfPresent(InfraContractDO::getResponsibleUserId, reqVO.getResponsibleUserId())
                .orderByDesc(InfraContractDO::getId));
    }

    default List<InfraContractDO> selectByResponsibleUser(Long userId) {
        return selectList(new LambdaQueryWrapperX<InfraContractDO>()
                .eq(InfraContractDO::getResponsibleUserId, userId)
                .orderByDesc(InfraContractDO::getSignDate));
    }

    /** 查询某用户某月的提成总额（commission_source=1 合同管理时使用） */
    default BigDecimal sumCommissionByUserAndMonth(Long userId, Integer yearMonth) {
        List<InfraContractDO> list = selectList(new LambdaQueryWrapperX<InfraContractDO>()
                .eq(InfraContractDO::getResponsibleUserId, userId)
                .eq(InfraContractDO::getCommissionYearMonth, yearMonth)
                .eq(InfraContractDO::getStatus, 1));
        return list.stream()
                .map(InfraContractDO::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 查询某用户所有生效合同，用于按时间比例计算本月提成
     * 返回 commission_amount > 0 的生效合同，由 Service 层在 Java 中计算日期交叉比例
     */
    default List<InfraContractDO> selectActiveWithCommissionByUser(Long userId) {
        return selectList(new LambdaQueryWrapperX<InfraContractDO>()
                .eq(InfraContractDO::getResponsibleUserId, userId)
                .eq(InfraContractDO::getStatus, 1)
                .gt(InfraContractDO::getCommissionAmount, BigDecimal.ZERO));
    }

}
