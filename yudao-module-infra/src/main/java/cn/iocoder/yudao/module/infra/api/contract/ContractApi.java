package cn.iocoder.yudao.module.infra.api.contract;

import cn.iocoder.yudao.module.infra.api.contract.dto.ContractSimpleDTO;

import java.util.List;

/**
 * 合同 API 接口，供其他模块调用
 */
public interface ContractApi {

    /**
     * 查询指定用户负责的合同简要列表
     *
     * @param userId 负责人用户ID
     * @return 合同简要列表
     */
    List<ContractSimpleDTO> getContractListByUserId(Long userId);

}
