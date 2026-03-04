package cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "绩效评分模板 Response VO")
@Data
public class PerformanceTemplateRespVO {

    @Schema(description = "模板ID")
    private Long id;
    @Schema(description = "模板名称")
    private String name;
    @Schema(description = "模板描述")
    private String description;
    @Schema(description = "绩效薪酬基准系数（默认0.45）")
    private BigDecimal performanceBaseRatio;
    @Schema(description = "分数-绩效系数映射规则JSON")
    private String coefficientRules;
    @Schema(description = "状态：0=启用 1=停用")
    private Integer status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "评分区块列表（含条目）")
    private List<PerformanceTemplateSectionVO> sections;
    @Schema(description = "应用此模板的员工用户ID列表")
    private List<Long> userIds;

}
