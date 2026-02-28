package cn.iocoder.yudao.module.infra.service.contract;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.contract.InfraContractDO;

import java.math.BigDecimal;
import java.util.List;

public interface InfraContractService {

    InfraContractRespVO createContract(InfraContractSaveReqVO createReqVO);

    void updateContract(InfraContractSaveReqVO updateReqVO);

    void deleteContract(Long id);

    InfraContractDO getContract(Long id);

    PageResult<InfraContractDO> getContractPage(InfraContractPageReqVO reqVO);

    /** 个人中心：我负责的合同列表 */
    List<InfraContractRespVO> getMyContractList(Long userId);

    /** 供薪资月结算调用：查询某用户某月的提成总额 */
    BigDecimal sumCommissionByUserAndMonth(Long userId, Integer yearMonth);

}
