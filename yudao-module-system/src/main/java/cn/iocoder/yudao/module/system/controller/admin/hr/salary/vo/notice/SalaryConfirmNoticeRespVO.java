package cn.iocoder.yudao.module.system.controller.admin.hr.salary.vo.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "薪资确认通知单 Response VO")
@Data
public class SalaryConfirmNoticeRespVO {

    @Schema(description = "通知单ID")
    private Long id;
    @Schema(description = "关联月度薪资ID")
    private Long salaryMonthlyId;
    @Schema(description = "员工用户ID")
    private Long userId;
    @Schema(description = "员工姓名（前端填充）")
    private String nickname;
    @Schema(description = "薪资所属年月（如202603）")
    private Integer yearMonth;
    @Schema(description = "应发薪资金额")
    private BigDecimal totalSalary;
    @Schema(description = "通知内容（JSON字符串，含各项明细）")
    private String noticeContent;
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    @Schema(description = "员工确认时间（null=未确认）")
    private LocalDateTime confirmTime;
    @Schema(description = "状态：0=待确认 1=已确认")
    private Integer status;
    @Schema(description = "备注")
    private String remark;

}
