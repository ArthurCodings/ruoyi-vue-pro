package cn.iocoder.yudao.module.infra.controller.admin.doc.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 在线文档分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InfraDocPageReqVO extends PageParam {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "文档类型：1=上传文件预览 2=外部链接 3=下载附件")
    private Integer type;

    @Schema(description = "状态：0=公开 1=仅内部")
    private Integer status;

    @Schema(description = "文档名称，模糊匹配")
    private String name;

}
