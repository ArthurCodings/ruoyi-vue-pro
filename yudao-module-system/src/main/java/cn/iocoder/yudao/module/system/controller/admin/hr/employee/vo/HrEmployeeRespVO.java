package cn.iocoder.yudao.module.system.controller.admin.hr.employee.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 员工花名册 Response VO")
@Data
@ExcelIgnoreUnannotated
public class HrEmployeeRespVO {

    @Schema(description = "主键")
    @ExcelProperty("员工ID")
    private Long id;
    @Schema(description = "关联系统用户ID")
    private Long userId;
    @Schema(description = "姓名")
    @ExcelProperty("姓名")
    private String nickname;
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "部门名称")
    @ExcelProperty("部门")
    private String deptName;
    @Schema(description = "岗位ID")
    private Long postId;
    @Schema(description = "岗位名称")
    @ExcelProperty("岗位")
    private String postName;
    @Schema(description = "手机号")
    @ExcelProperty("手机号")
    private String mobile;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "性别：0=未知 1=男 2=女")
    @ExcelProperty("性别")
    private Integer sex;
    @Schema(description = "头像URL")
    private String avatar;
    @Schema(description = "雇佣类型：1=全职 2=实习 3=兼职")
    @ExcelProperty("雇佣类型")
    private Integer employmentType;
    @Schema(description = "在职状态：1=在职 2=离职 3=待入职")
    @ExcelProperty("在职状态")
    private Integer employmentStatus;
    @Schema(description = "默认排班规则ID")
    private Long defaultRuleId;
    @Schema(description = "入职日期")
    @ExcelProperty("入职日期")
    private LocalDate joinDate;
    @Schema(description = "转正日期")
    private LocalDate formalDate;
    @Schema(description = "离职日期")
    private LocalDate leaveDate;
    @Schema(description = "身份证号（无敏感权限时脱敏）")
    private String idCard;
    @Schema(description = "紧急联系人")
    private String emergencyContact;
    @Schema(description = "紧急联系电话")
    private String emergencyPhone;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
