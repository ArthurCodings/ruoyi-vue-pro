package cn.iocoder.yudao.module.infra.dal.dataobject.contract;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同管理 DO
 */
@TableName("infra_contract")
@KeySequence("infra_contract_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class InfraContractDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 合同编号 */
    private String contractNo;
    /** 合同名称 */
    private String contractName;
    /** 合同类型：1=销售 2=采购 3=劳动 4=其他 */
    private Integer type;
    /** 服务类型：1=长期 2=短期 */
    private Integer serviceType;
    /** 客户/供应商名称 */
    private String customerName;
    /** 合同金额（元） */
    private BigDecimal amount;
    /** 签订日期 */
    private LocalDate signDate;
    /** 开始日期 */
    private LocalDate startDate;
    /** 结束日期 */
    private LocalDate endDate;
    /** 负责人用户ID */
    private Long responsibleUserId;
    /** 负责人姓名快照 */
    private String responsibleUsername;
    /** 合同附件URL */
    private String fileUrl;
    /** 提成比例（如 0.0500 = 5%） */
    private BigDecimal commissionRate;
    /** 提成金额（= amount × commissionRate） */
    private BigDecimal commissionAmount;
    /** 提成归属年月（0=按签订日期，非0=手动指定，保留兼容） */
    @TableField("`commission_year_month`")
    private Integer commissionYearMonth;
    /** 提成归属开始日期（为空时取 start_date，用于按时间区间归属提成） */
    private LocalDate commissionStartDate;
    /** 提成归属结束日期（为空时取 end_date） */
    private LocalDate commissionEndDate;
    /** 合同状态：0=草稿 1=生效 2=已完成 3=已终止 */
    private Integer status;
    /** 备注 */
    private String remark;

}
