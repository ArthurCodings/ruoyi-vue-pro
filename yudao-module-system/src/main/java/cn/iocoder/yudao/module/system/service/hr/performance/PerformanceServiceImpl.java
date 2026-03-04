package cn.iocoder.yudao.module.system.service.hr.performance;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.hr.performance.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.performance.*;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.EmployeeSalaryDO;
import cn.iocoder.yudao.module.system.dal.mysql.hr.performance.*;
import cn.iocoder.yudao.module.system.dal.mysql.hr.salary.EmployeeSalaryMapper;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class PerformanceServiceImpl implements PerformanceService {

    /** 默认绩效系数规则 */
    private static final String DEFAULT_COEFFICIENT_RULES =
            "[{\"minScore\":95,\"maxScore\":100,\"coefficient\":1.2}," +
            "{\"minScore\":90,\"maxScore\":94,\"coefficient\":1.0}," +
            "{\"minScore\":81,\"maxScore\":89,\"coefficient\":0.9}," +
            "{\"minScore\":71,\"maxScore\":80,\"coefficient\":0.7}," +
            "{\"minScore\":61,\"maxScore\":70,\"coefficient\":0.5}," +
            "{\"minScore\":21,\"maxScore\":60,\"coefficient\":0.2}," +
            "{\"minScore\":0,\"maxScore\":20,\"coefficient\":0.0}]";

    @Resource private PerformanceTemplateMapper templateMapper;
    @Resource private PerformanceTemplateSectionMapper sectionMapper;
    @Resource private PerformanceTemplateItemMapper itemMapper;
    @Resource private PerformanceTemplateUserMapper templateUserMapper;
    @Resource private PerformanceScoreMapper scoreMapper;
    @Resource private PerformanceScoreItemMapper scoreItemMapper;
    @Resource private EmployeeSalaryMapper employeeSalaryMapper;
    @Resource private AdminUserService adminUserService;
    @Resource private ObjectMapper objectMapper;

    // ===== 绩效模板 =====

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(PerformanceTemplateSaveReqVO reqVO) {
        PerformanceTemplateDO template = new PerformanceTemplateDO();
        template.setName(reqVO.getName());
        template.setDescription(reqVO.getDescription() != null ? reqVO.getDescription() : "");
        template.setPerformanceBaseRatio(reqVO.getPerformanceBaseRatio() != null
                ? reqVO.getPerformanceBaseRatio() : new BigDecimal("0.45"));
        template.setCoefficientRules(reqVO.getCoefficientRules() != null
                ? reqVO.getCoefficientRules() : DEFAULT_COEFFICIENT_RULES);
        template.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : 0);
        templateMapper.insert(template);
        saveSectionsAndItems(template.getId(), reqVO.getSections());
        if (reqVO.getUserIds() != null && !reqVO.getUserIds().isEmpty()) {
            setTemplateUsers(template.getId(), reqVO.getUserIds());
        }
        return template.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(PerformanceTemplateSaveReqVO reqVO) {
        PerformanceTemplateDO existing = templateMapper.selectById(reqVO.getId());
        if (existing == null) throw exception(HR_PERFORMANCE_TEMPLATE_NOT_FOUND);
        existing.setName(reqVO.getName());
        if (reqVO.getDescription() != null) existing.setDescription(reqVO.getDescription());
        if (reqVO.getPerformanceBaseRatio() != null) existing.setPerformanceBaseRatio(reqVO.getPerformanceBaseRatio());
        if (reqVO.getCoefficientRules() != null) existing.setCoefficientRules(reqVO.getCoefficientRules());
        if (reqVO.getStatus() != null) existing.setStatus(reqVO.getStatus());
        templateMapper.updateById(existing);
        // 全量覆盖区块和条目
        sectionMapper.deleteByTemplateId(reqVO.getId());
        itemMapper.deleteByTemplateId(reqVO.getId());
        saveSectionsAndItems(reqVO.getId(), reqVO.getSections());
        if (reqVO.getUserIds() != null) {
            setTemplateUsers(reqVO.getId(), reqVO.getUserIds());
        }
    }

    @Override
    public void deleteTemplate(Long id) {
        if (templateMapper.selectById(id) == null) throw exception(HR_PERFORMANCE_TEMPLATE_NOT_FOUND);
        templateMapper.deleteById(id);
    }

    @Override
    public PerformanceTemplateRespVO getTemplate(Long id) {
        PerformanceTemplateDO template = templateMapper.selectById(id);
        if (template == null) throw exception(HR_PERFORMANCE_TEMPLATE_NOT_FOUND);
        return buildTemplateRespVO(template);
    }

    @Override
    public List<PerformanceTemplateRespVO> getTemplateList() {
        return templateMapper.selectListByStatus(0).stream()
                .map(this::buildTemplateRespVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTemplateUsers(Long templateId, List<Long> userIds) {
        templateUserMapper.deleteByTemplateId(templateId);
        for (Long userId : userIds) {
            PerformanceTemplateUserDO rel = new PerformanceTemplateUserDO();
            rel.setTemplateId(templateId);
            rel.setUserId(userId);
            templateUserMapper.insert(rel);
        }
    }

    private void saveSectionsAndItems(Long templateId, List<PerformanceTemplateSectionVO> sections) {
        if (sections == null) return;
        int sectionSort = 0;
        for (PerformanceTemplateSectionVO sectionVO : sections) {
            PerformanceTemplateSectionDO section = new PerformanceTemplateSectionDO();
            section.setTemplateId(templateId);
            section.setSectionName(sectionVO.getSectionName());
            section.setSectionType(sectionVO.getSectionType());
            section.setMaxScore(sectionVO.getMaxScore());
            section.setSort(sectionVO.getSort() != null ? sectionVO.getSort() : sectionSort++);
            sectionMapper.insert(section);
            if (sectionVO.getItems() == null) continue;
            int itemSort = 0;
            for (PerformanceTemplateItemVO itemVO : sectionVO.getItems()) {
                PerformanceTemplateItemDO item = new PerformanceTemplateItemDO();
                item.setTemplateId(templateId);
                item.setSectionId(section.getId());
                item.setItemName(itemVO.getItemName());
                item.setMaxScore(itemVO.getMaxScore());
                item.setScoringCriteria(itemVO.getScoringCriteria());
                item.setEvidenceDesc(itemVO.getEvidenceDesc() != null ? itemVO.getEvidenceDesc() : "");
                item.setNotes(itemVO.getNotes() != null ? itemVO.getNotes() : "");
                item.setIsFixedScore(Boolean.TRUE.equals(itemVO.getIsFixedScore()));
                item.setFixedScoreCondition(itemVO.getFixedScoreCondition() != null ? itemVO.getFixedScoreCondition() : "");
                item.setSort(itemVO.getSort() != null ? itemVO.getSort() : itemSort++);
                itemMapper.insert(item);
            }
        }
    }

    private PerformanceTemplateRespVO buildTemplateRespVO(PerformanceTemplateDO template) {
        PerformanceTemplateRespVO vo = BeanUtils.toBean(template, PerformanceTemplateRespVO.class);
        List<PerformanceTemplateSectionDO> sections = sectionMapper.selectListByTemplateId(template.getId());
        List<PerformanceTemplateItemDO> allItems = itemMapper.selectListByTemplateId(template.getId());
        Map<Long, List<PerformanceTemplateItemDO>> itemsBySectionId = allItems.stream()
                .collect(Collectors.groupingBy(PerformanceTemplateItemDO::getSectionId));
        List<PerformanceTemplateSectionVO> sectionVOs = sections.stream().map(s -> {
            PerformanceTemplateSectionVO sv = BeanUtils.toBean(s, PerformanceTemplateSectionVO.class);
            List<PerformanceTemplateItemVO> itemVOs = itemsBySectionId.getOrDefault(s.getId(), Collections.emptyList())
                    .stream().map(i -> BeanUtils.toBean(i, PerformanceTemplateItemVO.class))
                    .collect(Collectors.toList());
            sv.setItems(itemVOs);
            return sv;
        }).collect(Collectors.toList());
        vo.setSections(sectionVOs);
        List<Long> userIds = templateUserMapper.selectListByTemplateId(template.getId())
                .stream().map(PerformanceTemplateUserDO::getUserId).collect(Collectors.toList());
        vo.setUserIds(userIds);
        return vo;
    }

    // ===== 绩效打分 =====

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveScore(PerformanceScoreSaveReqVO reqVO) {
        // 找到该员工绑定的模板
        PerformanceTemplateUserDO templateUser = templateUserMapper.selectByUserId(reqVO.getUserId());
        if (templateUser == null) throw exception(HR_PERFORMANCE_TEMPLATE_NOT_BOUND);
        Long templateId = templateUser.getTemplateId();
        PerformanceTemplateDO template = templateMapper.selectById(templateId);
        if (template == null) throw exception(HR_PERFORMANCE_TEMPLATE_NOT_FOUND);

        PerformanceScoreDO score;
        boolean isNew = false;
        if (reqVO.getId() != null) {
            score = scoreMapper.selectById(reqVO.getId());
            if (score == null) throw exception(HR_PERFORMANCE_SCORE_NOT_FOUND);
            if (Integer.valueOf(2).equals(score.getStatus())) throw exception(HR_PERFORMANCE_SCORE_ALREADY_APPROVED);
        } else {
            score = scoreMapper.selectByUserAndMonth(reqVO.getUserId(), reqVO.getYearMonth());
            if (score == null) {
                score = new PerformanceScoreDO();
                score.setUserId(reqVO.getUserId());
                score.setTemplateId(templateId);
                score.setYearMonth(reqVO.getYearMonth());
                score.setStatus(0);
                score.setReviewComment("");
                score.setAppealContent("");
                score.setAppealResult("");
                isNew = true;
            } else if (Integer.valueOf(2).equals(score.getStatus())) {
                throw exception(HR_PERFORMANCE_SCORE_ALREADY_APPROVED);
            }
        }

        if (reqVO.getAttendanceDeductionScore() != null) {
            score.setAttendanceDeductionScore(reqVO.getAttendanceDeductionScore());
        } else if (score.getAttendanceDeductionScore() == null) {
            score.setAttendanceDeductionScore(BigDecimal.ZERO);
        }
        if (reqVO.getReviewComment() != null) score.setReviewComment(reqVO.getReviewComment());

        // 覆盖打分条目
        if (isNew) {
            scoreMapper.insert(score);
        } else {
            scoreItemMapper.deleteByScoreId(score.getId());
        }
        // 保存打分条目并汇总各区块得分
        BigDecimal baseTotal = BigDecimal.ZERO, bonusTotal = BigDecimal.ZERO, deductTotal = BigDecimal.ZERO;
        Map<Long, PerformanceTemplateItemDO> itemMap = itemMapper.selectListByTemplateId(templateId)
                .stream().collect(Collectors.toMap(PerformanceTemplateItemDO::getId, i -> i));

        for (PerformanceScoreItemReqVO itemReq : reqVO.getItems()) {
            PerformanceTemplateItemDO templateItem = itemMap.get(itemReq.getTemplateItemId());
            if (templateItem == null) continue;
            PerformanceScoreItemDO item = new PerformanceScoreItemDO();
            item.setScoreId(score.getId());
            item.setTemplateItemId(itemReq.getTemplateItemId());
            item.setSectionType(templateItem.getSectionId() != null ? itemReq.getSectionType() : 1);
            // 优先用条目自带区块类型（来自模板）
            PerformanceTemplateSectionDO section = sectionMapper.selectById(templateItem.getSectionId());
            int sectionType = section != null ? section.getSectionType() : (itemReq.getSectionType() != null ? itemReq.getSectionType() : 1);
            item.setSectionType(sectionType);
            BigDecimal reviewScore = itemReq.getReviewScore() != null ? itemReq.getReviewScore() : BigDecimal.ZERO;
            BigDecimal maxScore = templateItem.getMaxScore() != null ? templateItem.getMaxScore() : BigDecimal.ZERO;
            // 确保审核分不超过满分
            if (reviewScore.compareTo(maxScore) > 0) reviewScore = maxScore;
            item.setSelfScore(itemReq.getSelfScore() != null ? itemReq.getSelfScore() : BigDecimal.ZERO);
            item.setReviewScore(reviewScore);
            item.setEvidenceUrl(itemReq.getEvidenceUrl() != null ? itemReq.getEvidenceUrl() : "");
            item.setRemark(itemReq.getRemark() != null ? itemReq.getRemark() : "");
            scoreItemMapper.insert(item);
            // 汇总
            switch (sectionType) {
                case 1: baseTotal   = baseTotal.add(reviewScore);   break;
                case 2: bonusTotal  = bonusTotal.add(reviewScore);  break;
                case 3: deductTotal = deductTotal.add(reviewScore); break;
            }
        }
        score.setBaseSectionScore(baseTotal);
        score.setBonusSectionScore(bonusTotal);
        score.setDeductionSectionScore(deductTotal);

        // 最终得分 = 基础指标 + 加分项 - 考勤扣分 - 扣分项
        BigDecimal finalScore = baseTotal
                .add(bonusTotal)
                .subtract(score.getAttendanceDeductionScore())
                .subtract(deductTotal)
                .setScale(2, RoundingMode.HALF_UP);
        if (finalScore.compareTo(BigDecimal.ZERO) < 0) finalScore = BigDecimal.ZERO;
        score.setFinalScore(finalScore);

        // 映射绩效系数
        BigDecimal coefficient = mapToCoefficient(finalScore, template.getCoefficientRules());
        score.setPerformanceCoefficient(coefficient);

        // 绩效薪酬 = 基本工资 × 绩效基准系数 × 绩效系数（与月度薪资计算逻辑一致）
        BigDecimal performanceSalary = calcPerformanceSalary(score.getUserId(), template, coefficient);
        score.setPerformanceSalary(performanceSalary);

        if (isNew) {
            scoreMapper.updateById(score);
        } else {
            scoreMapper.updateById(score);
        }
        return score.getId();
    }

    @Override
    public void submitScore(Long id) {
        PerformanceScoreDO score = scoreMapper.selectById(id);
        if (score == null) throw exception(HR_PERFORMANCE_SCORE_NOT_FOUND);
        if (!Integer.valueOf(0).equals(score.getStatus())) throw exception(HR_PERFORMANCE_SCORE_STATUS_ERROR);
        score.setStatus(1);
        scoreMapper.updateById(score);
    }

    @Override
    public void approveScore(Long id, String reviewComment, Long reviewerUserId) {
        PerformanceScoreDO score = scoreMapper.selectById(id);
        if (score == null) throw exception(HR_PERFORMANCE_SCORE_NOT_FOUND);
        if (!Integer.valueOf(1).equals(score.getStatus())) throw exception(HR_PERFORMANCE_SCORE_STATUS_ERROR);
        score.setStatus(2);
        if (reviewComment != null) score.setReviewComment(reviewComment);
        // 考核人：审核通过时记录当前操作用户
        if (reviewerUserId != null && reviewerUserId > 0) {
            score.setReviewerUserId(reviewerUserId);
            AdminUserDO reviewer = adminUserService.getUser(reviewerUserId);
            score.setReviewerName(reviewer != null && reviewer.getNickname() != null ? reviewer.getNickname() : "");
        }
        // 审核通过时重新计算绩效薪酬（确保使用最新薪资档案）
        PerformanceTemplateDO template = templateMapper.selectById(score.getTemplateId());
        if (template != null && score.getPerformanceCoefficient() != null) {
            BigDecimal performanceSalary = calcPerformanceSalary(score.getUserId(), template, score.getPerformanceCoefficient());
            score.setPerformanceSalary(performanceSalary);
        }
        scoreMapper.updateById(score);
    }

    @Override
    public void rejectScore(Long id, String reviewComment) {
        PerformanceScoreDO score = scoreMapper.selectById(id);
        if (score == null) throw exception(HR_PERFORMANCE_SCORE_NOT_FOUND);
        if (!Integer.valueOf(1).equals(score.getStatus())) throw exception(HR_PERFORMANCE_SCORE_STATUS_ERROR);
        score.setStatus(3);
        if (reviewComment != null) score.setReviewComment(reviewComment);
        scoreMapper.updateById(score);
    }

    @Override
    public void appealScore(Long id, Long userId, String appealContent) {
        PerformanceScoreDO score = scoreMapper.selectById(id);
        if (score == null) throw exception(HR_PERFORMANCE_SCORE_NOT_FOUND);
        if (!score.getUserId().equals(userId)) throw exception(HR_PERFORMANCE_SCORE_NOT_FOUND);
        score.setAppealContent(appealContent != null ? appealContent : "");
        scoreMapper.updateById(score);
    }

    @Override
    public PerformanceScoreRespVO getScore(Long id) {
        PerformanceScoreDO score = scoreMapper.selectById(id);
        if (score == null) throw exception(HR_PERFORMANCE_SCORE_NOT_FOUND);
        return buildScoreRespVO(score);
    }

    @Override
    public PageResult<PerformanceScoreRespVO> getScorePage(Integer yearMonth, Long userId, Integer status, PageParam pageParam) {
        PageResult<PerformanceScoreDO> page = scoreMapper.selectPage(yearMonth, userId, status, pageParam);
        List<PerformanceScoreRespVO> voList = page.getList().stream()
                .map(this::buildScoreRespVO).collect(Collectors.toList());
        return new PageResult<>(voList, page.getTotal());
    }

    @Override
    public PerformanceScoreRespVO getMyScore(Long userId, Integer yearMonth) {
        PerformanceScoreDO score = scoreMapper.selectByUserAndMonth(userId, yearMonth);
        return score != null ? buildScoreRespVO(score) : null;
    }

    private PerformanceScoreRespVO buildScoreRespVO(PerformanceScoreDO score) {
        PerformanceScoreRespVO vo = BeanUtils.toBean(score, PerformanceScoreRespVO.class);
        // 填充条目明细（含模板条目信息）
        List<PerformanceScoreItemDO> items = scoreItemMapper.selectListByScoreId(score.getId());
        Map<Long, PerformanceTemplateItemDO> itemMap = itemMapper.selectListByTemplateId(score.getTemplateId())
                .stream().collect(Collectors.toMap(PerformanceTemplateItemDO::getId, i -> i));
        List<PerformanceScoreItemDetailVO> detailList = items.stream().map(item -> {
            PerformanceScoreItemDetailVO d = BeanUtils.toBean(item, PerformanceScoreItemDetailVO.class);
            PerformanceTemplateItemDO tItem = itemMap.get(item.getTemplateItemId());
            if (tItem != null) {
                d.setItemName(tItem.getItemName());
                d.setMaxScore(tItem.getMaxScore());
                d.setScoringCriteria(tItem.getScoringCriteria());
                d.setEvidenceDesc(tItem.getEvidenceDesc());
                d.setNotes(tItem.getNotes());
                d.setIsFixedScore(tItem.getIsFixedScore());
            }
            return d;
        }).collect(Collectors.toList());
        vo.setItems(detailList);
        return vo;
    }

    /**
     * 计算绩效薪酬：绩效薪酬 = 基本工资 × 绩效基准系数 × 绩效系数（与 SalaryServiceImpl 月度薪资计算逻辑一致）
     */
    private BigDecimal calcPerformanceSalary(Long userId, PerformanceTemplateDO template, BigDecimal coefficient) {
        EmployeeSalaryDO salaryRecord = employeeSalaryMapper.selectByUserId(userId);
        BigDecimal baseSalary = salaryRecord != null && salaryRecord.getBaseSalary() != null
                ? salaryRecord.getBaseSalary() : BigDecimal.ZERO;
        BigDecimal ratio = template != null && template.getPerformanceBaseRatio() != null
                ? template.getPerformanceBaseRatio() : new BigDecimal("0.45");
        return baseSalary.multiply(ratio).multiply(coefficient).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 根据最终得分和绩效系数规则JSON映射对应系数
     */
    private BigDecimal mapToCoefficient(BigDecimal finalScore, String rulesJson) {
        try {
            String json = (rulesJson != null && !rulesJson.trim().isEmpty()) ? rulesJson : DEFAULT_COEFFICIENT_RULES;
            List<Map<String, Object>> rules = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            double score = finalScore.doubleValue();
            for (Map<String, Object> rule : rules) {
                double min = ((Number) rule.get("minScore")).doubleValue();
                double max = ((Number) rule.get("maxScore")).doubleValue();
                if (score >= min && score <= max) {
                    return new BigDecimal(rule.get("coefficient").toString());
                }
            }
        } catch (Exception e) {
            log.warn("[mapToCoefficient] 解析系数规则失败", e);
        }
        return BigDecimal.ZERO;
    }

}
