package cn.iocoder.yudao.module.system.dal.dataobject.hr.attendance;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * HR 法定节假日 DO
 */
@TableName("hr_holiday")
@KeySequence("hr_holiday_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class HolidayDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 节假日名称 */
    private String name;
    /** 具体日期 */
    private LocalDate holidayDate;
    /** 类型：1=休息日 2=调班工作日 */
    private Integer type;
    /** 所属年份，year 为 MySQL 保留字需反引号转义 */
    @TableField("`year`")
    private Integer year;
    /** 备注 */
    private String remark;

}
