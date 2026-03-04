package cn.iocoder.yudao.module.system.dal.dataobject.hr.performance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * HR 绩效打分记录 DO（按年月，每人每月一条）
 */
@TableName("hr_performance_score")
@KeySequence("hr_performance_score_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PerformanceScoreDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 被考核员工用户ID */
    private Long userId;
    /** 使用的绩效模板ID */
    private Long templateId;
    /** 考核年月（如202603） */
    @TableField("`year_month`")
    private Integer yearMonth;
    /** 考核人用户ID */
    private Long reviewerUserId;
    /** 考核人姓名（快照） */
    private String reviewerName;
    /** 基础指标审核得分合计 */
    private BigDecimal baseSectionScore;
    /** 加分项审核得分合计（含全勤固定分） */
    private BigDecimal bonusSectionScore;
    /** 扣分项审核得分合计 */
    private BigDecimal deductionSectionScore;
    /** 考勤扣分（由人事部门根据打卡记录填写） */
    private BigDecimal attendanceDeductionScore;
    /** 最终得分 = 基础指标 + 加分项 - 考勤扣分 - 扣分项 */
    private BigDecimal finalScore;
    /** 绩效系数（由finalScore自动映射，如95-100→1.2） */
    private BigDecimal performanceCoefficient;
    /** 绩效薪酬 = 基本薪资 × performanceBaseRatio × 绩效系数 */
    private BigDecimal performanceSalary;
    /**
     * 状态（0=草稿 1=已提交 2=审核通过 3=已驳回）
     */
    private Integer status;
    /** 审核意见 */
    private String reviewComment;
    /** 员工申诉内容 */
    private String appealContent;
    /** 申诉处理结果 */
    private String appealResult;

}
