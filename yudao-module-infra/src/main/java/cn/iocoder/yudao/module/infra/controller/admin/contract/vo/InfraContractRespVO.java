package cn.iocoder.yudao.module.infra.controller.admin.contract.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 合同 Response VO")
@Data
@ExcelIgnoreUnannotated
public class InfraContractRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "合同编号")
    @ExcelProperty("合同编号")
    private String contractNo;
    @Schema(description = "合同名称")
    @ExcelProperty("合同名称")
    private String contractName;
    @Schema(description = "合同类型")
    @ExcelProperty("合同类型")
    private Integer type;
    @Schema(description = "客户/供应商名称")
    @ExcelProperty("客户名称")
    private String customerName;
    @Schema(description = "合同金额（元）")
    @ExcelProperty("合同金额")
    private BigDecimal amount;
    @Schema(description = "签订日期")
    @ExcelProperty("签订日期")
    private LocalDate signDate;
    @Schema(description = "开始日期")
    private LocalDate startDate;
    @Schema(description = "结束日期")
    @ExcelProperty("到期日期")
    private LocalDate endDate;
    @Schema(description = "负责人用户ID")
    private Long responsibleUserId;
    @Schema(description = "负责人姓名")
    @ExcelProperty("负责人")
    private String responsibleUsername;
    @Schema(description = "合同附件URL")
    private String fileUrl;
    @Schema(description = "提成比例")
    private BigDecimal commissionRate;
    @Schema(description = "提成金额（元）")
    @ExcelProperty("提成金额")
    private BigDecimal commissionAmount;
    @Schema(description = "提成归属年月")
    private Integer commissionYearMonth;
    @Schema(description = "提成归属开始日期")
    private LocalDate commissionStartDate;
    @Schema(description = "提成归属结束日期")
    private LocalDate commissionEndDate;
    @Schema(description = "合同状态")
    @ExcelProperty("状态")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
