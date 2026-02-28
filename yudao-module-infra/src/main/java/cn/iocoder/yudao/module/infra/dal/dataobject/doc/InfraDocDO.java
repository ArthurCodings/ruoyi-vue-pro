package cn.iocoder.yudao.module.infra.dal.dataobject.doc;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 在线文档 DO
 */
@TableName("infra_doc")
@KeySequence("infra_doc_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class InfraDocDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 所属分类ID */
    private Long categoryId;
    /** 文档名称 */
    private String name;
    /** 文档类型：1=上传文件预览 2=外部链接 3=下载附件 */
    private Integer type;
    /** 文件URL */
    private String fileUrl;
    /** 文件扩展名（docx/xlsx/pdf） */
    private String fileType;
    /** 外部链接 */
    private String externalUrl;
    /** 文件大小（字节） */
    private Long fileSize;
    /** 排序 */
    private Integer sort;
    /** 状态：0=公开 1=仅内部 */
    private Integer status;
    /** 浏览次数 */
    private Integer viewCount;
    /** 简介说明 */
    private String remark;

}
