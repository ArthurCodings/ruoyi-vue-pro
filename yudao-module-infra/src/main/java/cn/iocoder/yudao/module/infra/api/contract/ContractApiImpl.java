package cn.iocoder.yudao.module.infra.api.contract;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.api.contract.dto.ContractSimpleDTO;
import cn.iocoder.yudao.module.infra.dal.dataobject.contract.InfraContractDO;
import cn.iocoder.yudao.module.infra.service.contract.InfraContractService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class ContractApiImpl implements ContractApi {

    @Resource
    private InfraContractService contractService;

    @Override
    public List<ContractSimpleDTO> getContractListByUserId(Long userId) {
        cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractPageReqVO reqVO =
                new cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractPageReqVO();
        reqVO.setResponsibleUserId(userId);
        reqVO.setPageSize(cn.iocoder.yudao.framework.common.pojo.PageParam.PAGE_SIZE_NONE);
        List<InfraContractDO> list = contractService.getContractPage(reqVO).getList();
        return BeanUtils.toBean(list, ContractSimpleDTO.class);
    }

}
