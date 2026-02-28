package cn.iocoder.yudao.module.infra.controller.admin.contract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 合同创建/修改 Request VO")
@Data
public class InfraContractSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "合同编号")
    private String contractNo;

    @Schema(description = "合同名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    @Schema(description = "合同类型：1=销售 2=采购 3=劳动 4=其他", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "合同类型不能为空")
    private Integer type;

    @Schema(description = "客户/供应商名称")
    private String customerName;

    @Schema(description = "合同金额（元）")
    private BigDecimal amount;

    @Schema(description = "签订日期")
    private LocalDate signDate;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "负责人用户ID")
    private Long responsibleUserId;

    @Schema(description = "合同附件URL")
    private String fileUrl;

    @Schema(description = "提成比例（如 0.05 = 5%）")
    private BigDecimal commissionRate;

    @Schema(description = "提成归属年月（0=按签订日期归属，保留兼容）")
    private Integer commissionYearMonth;
    @Schema(description = "提成归属开始日期（为空时取 start_date）")
    private LocalDate commissionStartDate;
    @Schema(description = "提成归属结束日期（为空时取 end_date）")
    private LocalDate commissionEndDate;

    @Schema(description = "合同状态：0=草稿 1=生效 2=已完成 3=已终止")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

}
