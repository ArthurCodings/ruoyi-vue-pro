package cn.iocoder.yudao.module.system.dal.dataobject.hr.salary;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * HR 薪资确认通知单 DO
 * 当 HR 确认薪资时，先生成此通知单推送给员工；员工确认后更新月度薪资状态
 */
@TableName("hr_salary_confirm_notice")
@KeySequence("hr_salary_confirm_notice_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SalaryConfirmNoticeDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 关联月度薪资ID（hr_salary_monthly.id） */
    private Long salaryMonthlyId;
    /** 接收通知的员工用户ID */
    private Long userId;
    /** 薪资所属年月（如202603） */
    @TableField("`year_month`")
    private Integer yearMonth;
    /** 应发薪资快照（发送时取值） */
    private BigDecimal totalSalary;
    /** 通知内容（JSON字符串，含各项薪资明细快照，供员工查看） */
    private String noticeContent;
    /** 发送时间 */
    private LocalDateTime sendTime;
    /** 员工确认时间（null=未确认） */
    private LocalDateTime confirmTime;
    /** 状态（0=待确认 1=已确认） */
    private Integer status;
    /** 备注 */
    private String remark;

}
