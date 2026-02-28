package cn.iocoder.yudao.module.infra.api.contract.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同简要信息 DTO（供跨模块传递）
 */
@Data
public class ContractSimpleDTO {

    /** 合同ID */
    private Long id;
    /** 合同编号 */
    private String contractNo;
    /** 合同名称 */
    private String contractName;
    /** 合同类型：1=销售 2=采购 3=劳动 4=其他 */
    private Integer type;
    /** 客户/供应商名称 */
    private String customerName;
    /** 合同金额（元） */
    private BigDecimal amount;
    /** 签订日期 */
    private LocalDate signDate;
    /** 合同结束日期 */
    private LocalDate endDate;
    /** 提成金额（元） */
    private BigDecimal commissionAmount;
    /** 合同状态：0=草稿 1=生效 2=已完成 3=已终止 */
    private Integer status;

}
