package cn.iocoder.yudao.module.infra.controller.admin.doc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 在线文档 Response VO")
@Data
public class InfraDocRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "所属分类ID")
    private Long categoryId;
    @Schema(description = "文档名称")
    private String name;
    @Schema(description = "文档类型：1=上传文件预览 2=外部链接 3=下载附件")
    private Integer type;
    @Schema(description = "文件URL")
    private String fileUrl;
    @Schema(description = "文件扩展名")
    private String fileType;
    @Schema(description = "外部链接")
    private String externalUrl;
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "状态：0=公开 1=仅内部")
    private Integer status;
    @Schema(description = "浏览次数")
    private Integer viewCount;
    @Schema(description = "简介说明")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
