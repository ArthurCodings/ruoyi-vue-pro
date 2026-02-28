package cn.iocoder.yudao.module.infra.service.contract;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.api.user.UserNicknameApi;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.contract.vo.InfraContractSaveReqVO;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.contract.InfraContractDO;
import cn.iocoder.yudao.module.infra.dal.mysql.contract.InfraContractMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Validated
public class InfraContractServiceImpl implements InfraContractService {

    @Resource
    private InfraContractMapper contractMapper;
    @Autowired(required = false)
    private UserNicknameApi userNicknameApi;

    @Override
    public InfraContractRespVO createContract(InfraContractSaveReqVO createReqVO) {
        InfraContractDO contract = BeanUtils.toBean(createReqVO, InfraContractDO.class);
        // 创建时若未填合同编号则自动生成
        if (contract.getContractNo() == null || contract.getContractNo().trim().isEmpty()) {
            contract.setContractNo(generateContractNo());
        }
        // 根据负责人ID填充负责人姓名快照
        fillResponsibleUsername(contract);
        // 自动计算提成金额
        calcCommission(contract);
        // 提成归属年月默认按签订日期（保留兼容）
        if ((contract.getCommissionYearMonth() == null || contract.getCommissionYearMonth() == 0)
                && contract.getSignDate() != null) {
            contract.setCommissionYearMonth(
                    contract.getSignDate().getYear() * 100 + contract.getSignDate().getMonthValue());
        }
        // 提成归属时间区间：为空时默认取合同开始/结束日期
        if (contract.getCommissionStartDate() == null && contract.getStartDate() != null) {
            contract.setCommissionStartDate(contract.getStartDate());
        }
        if (contract.getCommissionEndDate() == null && contract.getEndDate() != null) {
            contract.setCommissionEndDate(contract.getEndDate());
        }
        contractMapper.insert(contract);
        return BeanUtils.toBean(contract, InfraContractRespVO.class);
    }

    @Override
    public void updateContract(InfraContractSaveReqVO updateReqVO) {
        InfraContractDO update = BeanUtils.toBean(updateReqVO, InfraContractDO.class);
        fillResponsibleUsername(update);
        calcCommission(update);
        // 提成归属时间区间：为空时默认取合同开始/结束日期
        if (update.getCommissionStartDate() == null && update.getStartDate() != null) {
            update.setCommissionStartDate(update.getStartDate());
        }
        if (update.getCommissionEndDate() == null && update.getEndDate() != null) {
            update.setCommissionEndDate(update.getEndDate());
        }
        contractMapper.updateById(update);
    }

    /** 生成合同编号：HT + yyyyMMddHHmmss */
    private String generateContractNo() {
        return "HT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /** 根据 responsible_user_id 填充 responsible_username 快照 */
    private void fillResponsibleUsername(InfraContractDO contract) {
        if (contract.getResponsibleUserId() != null && contract.getResponsibleUserId() > 0
                && userNicknameApi != null) {
            String nickname = userNicknameApi.getNickname(contract.getResponsibleUserId());
            contract.setResponsibleUsername(nickname != null ? nickname : "");
        }
    }

    @Override
    public void deleteContract(Long id) {
        contractMapper.deleteById(id);
    }

    @Override
    public InfraContractDO getContract(Long id) {
        return contractMapper.selectById(id);
    }

    @Override
    public PageResult<InfraContractDO> getContractPage(InfraContractPageReqVO reqVO) {
        return contractMapper.selectPage(reqVO);
    }

    @Override
    public List<InfraContractRespVO> getMyContractList(Long userId) {
        List<InfraContractDO> list = contractMapper.selectByResponsibleUser(userId);
        return BeanUtils.toBean(list, InfraContractRespVO.class);
    }

    @Override
    public BigDecimal sumCommissionByUserAndMonth(Long userId, Integer yearMonth) {
        List<InfraContractDO> list = contractMapper.selectList(
                new LambdaQueryWrapperX<InfraContractDO>()
                        .eq(InfraContractDO::getResponsibleUserId, userId)
                        .eq(InfraContractDO::getStatus, 1));
        YearMonth ym = YearMonth.of(yearMonth / 100, yearMonth % 100);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();
        BigDecimal sum = BigDecimal.ZERO;
        for (InfraContractDO c : list) {
            BigDecimal amt = c.getCommissionAmount();
            if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0) continue;
            LocalDate start = c.getCommissionStartDate() != null ? c.getCommissionStartDate() : c.getStartDate();
            LocalDate end = c.getCommissionEndDate() != null ? c.getCommissionEndDate() : c.getEndDate();
            if (start != null && end != null) {
                // 按时间区间：若该月与归属区间有重叠，按比例分摊
                LocalDate overlapStart = start.isAfter(monthStart) ? start : monthStart;
                LocalDate overlapEnd = end.isBefore(monthEnd) ? end : monthEnd;
                if (!overlapStart.isAfter(overlapEnd)) {
                    long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
                    long overlapDays = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
                    if (totalDays > 0) {
                        sum = sum.add(amt.multiply(BigDecimal.valueOf(overlapDays))
                                .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP));
                    }
                }
            } else if (Integer.valueOf(yearMonth).equals(c.getCommissionYearMonth())) {
                // 兼容旧数据：按 commission_year_month 精确匹配
                sum = sum.add(amt);
            }
        }
        return sum;
    }

    private void calcCommission(InfraContractDO contract) {
        if (contract.getAmount() != null && contract.getCommissionRate() != null) {
            contract.setCommissionAmount(contract.getAmount().multiply(contract.getCommissionRate()));
        } else {
            contract.setCommissionAmount(BigDecimal.ZERO);
        }
    }

}
