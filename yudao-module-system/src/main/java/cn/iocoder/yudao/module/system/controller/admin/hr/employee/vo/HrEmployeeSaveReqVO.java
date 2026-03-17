package cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 员工花名册创建/修改 Request VO")
@Data
public class HrEmployeeSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "关联系统用户ID")
    private Long userId;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "姓名不能为空")
    private String nickname;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "岗位ID")
    private Long postId;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "性别：0=未知 1=男 2=女")
    private Integer sex;

    @Schema(description = "雇佣类型：1=全职 2=实习 3=兼职", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "雇佣类型不能为空")
    private Integer employmentType;

    @Schema(description = "在职状态：1=在职 2=离职 3=待入职", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "在职状态不能为空")
    private Integer employmentStatus;

    @Schema(description = "默认排班规则ID（0=使用系统默认晚班）")
    private Long defaultRuleId;

    @Schema(description = "入职日期")
    private LocalDate joinDate;

    @Schema(description = "转正日期")
    private LocalDate formalDate;

    @Schema(description = "离职日期")
    private LocalDate leaveDate;

    @Schema(description = "银行卡号")
    private String bankCardNo;

    @Schema(description = "开户行")
    private String bankName;

    @Schema(description = "学历（如：本科、硕士）")
    private String education;

    @Schema(description = "现居住地")
    private String currentAddress;

    @Schema(description = "户口类型（如：城镇、农村）")
    private String householdType;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "紧急联系人姓名")
    private String emergencyContact;

    @Schema(description = "紧急联系电话")
    private String emergencyPhone;

    @Schema(description = "备注")
    private String remark;

}
