package cn.iocoder.yudao.module.infra.controller.admin.contract.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 合同分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InfraContractPageReqVO extends PageParam {

    @Schema(description = "合同名称，模糊匹配")
    private String contractName;

    @Schema(description = "合同类型：1=销售 2=采购 3=劳动 4=其他")
    private Integer type;

    @Schema(description = "合同状态：0=草稿 1=生效 2=已完成 3=已终止")
    private Integer status;

    @Schema(description = "负责人用户ID")
    private Long responsibleUserId;

}
