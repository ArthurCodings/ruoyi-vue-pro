package cn.iocoder.yudao.module.system.dal.dataobject.hr.performance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * HR 绩效打分条目明细 DO
 */
@TableName("hr_performance_score_item")
@KeySequence("hr_performance_score_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PerformanceScoreItemDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 关联绩效打分记录ID */
    private Long scoreId;
    /** 关联模板条目ID */
    private Long templateItemId;
    /** 区块类型（1=基础指标 2=加分项 3=扣分项，冗余便于统计） */
    private Integer sectionType;
    /** 初得分（被考核人自报/初评） */
    private BigDecimal selfScore;
    /** 审核分（考核人审定） */
    private BigDecimal reviewScore;
    /** 佐证材料URL（多文件逗号分隔） */
    private String evidenceUrl;
    /** 备注 */
    private String remark;

}
