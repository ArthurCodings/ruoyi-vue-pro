package cn.iocoder.yudao.module.system.dal.dataobject.hr.performance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * HR 绩效模板评分条目 DO
 */
@TableName("hr_performance_template_item")
@KeySequence("hr_performance_template_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PerformanceTemplateItemDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 关联模板ID */
    private Long templateId;
    /** 关联区块ID */
    private Long sectionId;
    /** 评价指标名称（如：账务处理合规与时效） */
    private String itemName;
    /** 该条目满分 */
    private BigDecimal maxScore;
    /** 量化评价标准描述 */
    private String scoringCriteria;
    /** 佐证材料说明 */
    private String evidenceDesc;
    /** 备注（如：重大差错本项得0分） */
    private String notes;
    /**
     * 是否为固定分（true=由系统自动计算，如全勤固定2分；false=需人工打分）
     */
    private Boolean isFixedScore;
    /** 固定分值条件说明（isFixedScore=true时有效） */
    private String fixedScoreCondition;
    /** 显示排序 */
    private Integer sort;

}
