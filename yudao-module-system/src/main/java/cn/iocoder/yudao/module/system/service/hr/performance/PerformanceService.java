package cn.iocoder.yudao.module.system.service.hr.performance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo.PerformanceScoreRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo.PerformanceScoreSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo.PerformanceTemplateRespVO;
import cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo.PerformanceTemplateSaveReqVO;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

import java.util.List;

public interface PerformanceService {

    // ---- 绩效模板 ----

    /** 创建绩效评分模板（含区块和条目） */
    Long createTemplate(PerformanceTemplateSaveReqVO reqVO);

    /** 修改绩效评分模板 */
    void updateTemplate(PerformanceTemplateSaveReqVO reqVO);

    /** 删除绩效评分模板（逻辑删除） */
    void deleteTemplate(Long id);

    /** 获取模板详情（含区块和条目） */
    PerformanceTemplateRespVO getTemplate(Long id);

    /** 获取所有启用的模板列表 */
    List<PerformanceTemplateRespVO> getTemplateList();

    /** 设置模板应用人员（全量覆盖） */
    void setTemplateUsers(Long templateId, List<Long> userIds);

    // ---- 绩效打分 ----

    /** 创建/修改绩效打分记录 */
    Long saveScore(PerformanceScoreSaveReqVO reqVO);

    /** 提交绩效打分（草稿→已提交） */
    void submitScore(Long id);

    /** 审核通过绩效打分（考核人为当前操作用户） */
    void approveScore(Long id, String reviewComment, Long reviewerUserId);

    /** 驳回绩效打分 */
    void rejectScore(Long id, String reviewComment);

    /** 员工提交申诉 */
    void appealScore(Long id, Long userId, String appealContent);

    /** 获取绩效打分详情 */
    PerformanceScoreRespVO getScore(Long id);

    /** 分页查询绩效打分列表 */
    PageResult<PerformanceScoreRespVO> getScorePage(Integer yearMonth, Long userId, Integer status, PageParam pageParam);

    /** 个人中心：获取本人某月的绩效打分 */
    PerformanceScoreRespVO getMyScore(Long userId, Integer yearMonth);

}
