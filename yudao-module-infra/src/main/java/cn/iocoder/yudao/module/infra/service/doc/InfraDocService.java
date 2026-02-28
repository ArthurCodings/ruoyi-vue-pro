package cn.iocoder.yudao.module.infra.service.doc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.DocCategoryRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.DocCategorySaveReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.InfraDocPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.InfraDocSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.doc.DocCategoryDO;
import cn.iocoder.yudao.module.infra.dal.dataobject.doc.InfraDocDO;

import java.util.List;

public interface InfraDocService {

    // 分类
    Long createCategory(DocCategorySaveReqVO createReqVO);
    void updateCategory(DocCategorySaveReqVO updateReqVO);
    void deleteCategory(Long id);
    List<DocCategoryRespVO> getCategoryTree();

    // 文档
    Long createDoc(InfraDocSaveReqVO createReqVO);
    void updateDoc(InfraDocSaveReqVO updateReqVO);
    void deleteDoc(Long id);
    InfraDocDO getDoc(Long id);
    PageResult<InfraDocDO> getDocPage(InfraDocPageReqVO reqVO);
    void incrementViewCount(Long id);

}
