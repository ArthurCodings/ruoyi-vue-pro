package cn.iocoder.yudao.module.infra.service.doc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.DocCategoryRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.DocCategorySaveReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.InfraDocPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.doc.vo.InfraDocSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.doc.DocCategoryDO;
import cn.iocoder.yudao.module.infra.dal.dataobject.doc.InfraDocDO;
import cn.iocoder.yudao.module.infra.dal.mysql.doc.DocCategoryMapper;
import cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.infra.dal.mysql.doc.InfraDocMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
public class InfraDocServiceImpl implements InfraDocService {

    @Resource
    private DocCategoryMapper categoryMapper;
    @Resource
    private InfraDocMapper docMapper;

    @Override
    public Long createCategory(DocCategorySaveReqVO createReqVO) {
        DocCategoryDO category = BeanUtils.toBean(createReqVO, DocCategoryDO.class);
        if (category.getParentId() == null) category.setParentId(0L);
        if (category.getSort() == null) category.setSort(0);
        if (category.getStatus() == null) category.setStatus(0);
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateCategory(DocCategorySaveReqVO updateReqVO) {
        if (updateReqVO.getId() == null) {
            throw exception(ErrorCodeConstants.DOC_CATEGORY_NOT_EXISTS);
        }
        DocCategoryDO existing = categoryMapper.selectById(updateReqVO.getId());
        if (existing == null) {
            throw exception(ErrorCodeConstants.DOC_CATEGORY_NOT_EXISTS);
        }
        DocCategoryDO update = BeanUtils.toBean(updateReqVO, DocCategoryDO.class);
        categoryMapper.updateById(update);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public List<DocCategoryRespVO> getCategoryTree() {
        List<DocCategoryDO> all = categoryMapper.selectAll();
        List<DocCategoryRespVO> vos = BeanUtils.toBean(all, DocCategoryRespVO.class);
        Map<Long, List<DocCategoryRespVO>> childrenMap = vos.stream()
                .collect(Collectors.groupingBy(DocCategoryRespVO::getParentId));
        vos.forEach(vo -> vo.setChildren(childrenMap.getOrDefault(vo.getId(), new ArrayList<>())));
        return vos.stream().filter(vo -> vo.getParentId() == 0).collect(Collectors.toList());
    }

    @Override
    public Long createDoc(InfraDocSaveReqVO createReqVO) {
        InfraDocDO doc = BeanUtils.toBean(createReqVO, InfraDocDO.class);
        if (doc.getViewCount() == null) doc.setViewCount(0);
        docMapper.insert(doc);
        return doc.getId();
    }

    @Override
    public void updateDoc(InfraDocSaveReqVO updateReqVO) {
        docMapper.updateById(BeanUtils.toBean(updateReqVO, InfraDocDO.class));
    }

    @Override
    public void deleteDoc(Long id) {
        docMapper.deleteById(id);
    }

    @Override
    public InfraDocDO getDoc(Long id) {
        return docMapper.selectById(id);
    }

    @Override
    public PageResult<InfraDocDO> getDocPage(InfraDocPageReqVO reqVO) {
        return docMapper.selectPage(reqVO);
    }

    @Override
    public void incrementViewCount(Long id) {
        InfraDocDO doc = docMapper.selectById(id);
        if (doc != null) {
            doc.setViewCount(doc.getViewCount() + 1);
            docMapper.updateById(doc);
        }
    }

}
