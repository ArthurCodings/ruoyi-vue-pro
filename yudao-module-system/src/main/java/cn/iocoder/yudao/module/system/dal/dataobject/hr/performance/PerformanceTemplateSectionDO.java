package cn.iocoder.yudao.module.system.dal.dataobject.hr.performance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * HR 绩效模板评分区块 DO（基础指标/加分项/扣分项）
 */
@TableName("hr_performance_template_section")
@KeySequence("hr_performance_template_section_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PerformanceTemplateSectionDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 关联模板ID */
    private Long templateId;
    /** 区块名称（如：一、基础指标（100分）） */
    private String sectionName;
    /** 区块类型（1=基础指标 2=加分项 3=扣分项） */
    private Integer sectionType;
    /** 区块最高分 */
    private BigDecimal maxScore;
    /** 显示排序 */
    private Integer sort;

}
