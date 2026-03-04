package cn.iocoder.yudao.module.system.dal.dataobject.hr.performance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * HR 绩效模板应用人员 DO（一个模板可绑定多个员工）
 */
@TableName("hr_performance_template_user")
@KeySequence("hr_performance_template_user_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PerformanceTemplateUserDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 关联模板ID */
    private Long templateId;
    /** 应用该模板的员工用户ID */
    private Long userId;

}
