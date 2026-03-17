package cn.iocoder.yudao.module.system.dal.dataobject.hr.employee;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * HR 员工花名册 DO
 */
@TableName("hr_employee")
@KeySequence("hr_employee_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class HrEmployeeDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 关联系统用户ID（用户被删除后置0） */
    private Long userId;
    /** 姓名快照 */
    private String nickname;
    /** 部门ID快照 */
    private Long deptId;
    /** 部门名称快照 */
    private String deptName;
    /** 岗位ID */
    private Long postId;
    /** 岗位名称快照 */
    private String postName;
    /** 手机号 */
    private String mobile;
    /** 邮箱 */
    private String email;
    /** 性别：0=未知 1=男 2=女 */
    private Integer sex;
    /** 头像URL */
    private String avatar;
    /** 雇佣类型：1=全职 2=实习 3=兼职 */
    private Integer employmentType;
    /** 在职状态：1=在职 2=离职 3=待入职 */
    private Integer employmentStatus;
    /** 默认排班规则ID（0=使用系统默认晚班） */
    private Long defaultRuleId;
    /** 入职日期 */
    private LocalDate joinDate;
    /** 转正日期 */
    private LocalDate formalDate;
    /** 离职日期 */
    private LocalDate leaveDate;
    /** 银行卡号 */
    private String bankCardNo;
    /** 开户行 */
    private String bankName;
    /** 学历（如：本科、硕士） */
    private String education;
    /** 现居住地 */
    private String currentAddress;
    /** 户口类型（如：城镇、农村） */
    private String householdType;
    /** 身份证号（敏感字段） */
    private String idCard;
    /** 紧急联系人姓名 */
    private String emergencyContact;
    /** 紧急联系电话 */
    private String emergencyPhone;
    /** 备注 */
    private String remark;

}
