package cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "绩效打分记录 Response VO")
@Data
public class PerformanceScoreRespVO {

    @Schema(description = "打分记录ID")
    private Long id;
    @Schema(description = "被考核员工用户ID")
    private Long userId;
    @Schema(description = "员工姓名（前端填充）")
    private String nickname;
    @Schema(description = "使用的绩效模板ID")
    private Long templateId;
    @Schema(description = "模板名称（前端填充）")
    private String templateName;
    @Schema(description = "考核年月（如202603）")
    private Integer yearMonth;
    @Schema(description = "考核人用户ID")
    private Long reviewerUserId;
    @Schema(description = "考核人姓名")
    private String reviewerName;
    @Schema(description = "基础指标审核得分合计")
    private BigDecimal baseSectionScore;
    @Schema(description = "加分项审核得分合计")
    private BigDecimal bonusSectionScore;
    @Schema(description = "扣分项审核得分合计")
    private BigDecimal deductionSectionScore;
    @Schema(description = "考勤扣分")
    private BigDecimal attendanceDeductionScore;
    @Schema(description = "最终得分")
    private BigDecimal finalScore;
    @Schema(description = "绩效系数")
    private BigDecimal performanceCoefficient;
    @Schema(description = "绩效薪酬")
    private BigDecimal performanceSalary;
    @Schema(description = "状态：0=草稿 1=已提交 2=审核通过 3=已驳回")
    private Integer status;
    @Schema(description = "审核意见")
    private String reviewComment;
    @Schema(description = "员工申诉内容")
    private String appealContent;
    @Schema(description = "申诉处理结果")
    private String appealResult;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "打分条目明细（含模板条目信息）")
    private List<PerformanceScoreItemDetailVO> items;

}
