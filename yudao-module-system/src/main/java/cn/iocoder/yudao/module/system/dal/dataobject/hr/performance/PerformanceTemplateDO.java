package cn.iocoder.yudao.module.system.dal.dataobject.hr.performance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * HR 绩效评分模板 DO
 */
@TableName("hr_performance_template")
@KeySequence("hr_performance_template_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PerformanceTemplateDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 模板名称（如：财务岗绩效模板、管理岗绩效模板） */
    private String name;
    /** 模板描述 */
    private String description;
    /**
     * 绩效薪酬基准系数（绩效薪酬 = 基本薪资 × 此系数 × 绩效系数，默认0.45）
     */
    private BigDecimal performanceBaseRatio;
    /**
     * 分数-绩效系数映射规则（JSON数组字符串）
     * 格式：[{"minScore":95,"maxScore":100,"coefficient":1.2}, ...]
     * 默认规则：95-100→1.2, 90-94→1.0, 81-89→0.9, 71-80→0.7, 61-70→0.5, 21-60→0.2, 0-20→0.0
     */
    private String coefficientRules;
    /** 状态（0=启用 1=停用） */
    private Integer status;

}
