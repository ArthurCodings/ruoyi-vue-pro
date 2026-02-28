package cn.iocoder.yudao.module.infra.dal.dataobject.doc;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 在线文档分类 DO
 */
@TableName("infra_doc_category")
@KeySequence("infra_doc_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class DocCategoryDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 分类名称 */
    private String name;
    /** 父分类ID（0=根分类） */
    private Long parentId;
    /** 排序 */
    private Integer sort;
    /** 状态：0=启用 1=停用 */
    private Integer status;

}
