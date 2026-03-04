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
        YearMonth targetYm = YearMonth.of(yearMonth / 100, yearMonth % 100);
        BigDecimal sum = BigDecimal.ZERO;
        for (InfraContractDO c : list) {
            BigDecimal amt = c.getCommissionAmount();
            if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0) continue;
            LocalDate start = c.getCommissionStartDate() != null ? c.getCommissionStartDate() : c.getStartDate();
            LocalDate end = c.getCommissionEndDate() != null ? c.getCommissionEndDate() : c.getEndDate();
            if (start != null && end != null) {
                // 按涉及的月份个数平均分配：归属期跨越几个月，则每月提成 = 总提成 / 月份数
                int monthCount = countMonthsInRange(start, end);
                if (monthCount > 0 && isMonthInRange(targetYm, start, end)) {
                    sum = sum.add(amt.divide(BigDecimal.valueOf(monthCount), 2, RoundingMode.HALF_UP));
                }
            } else if (Integer.valueOf(yearMonth).equals(c.getCommissionYearMonth())) {
                // 兼容旧数据：按 commission_year_month 精确匹配，当月100%
                sum = sum.add(amt);
            }
        }
        return sum;
    }

    /** 计算日期区间涉及的月份个数（如 2-14 到 4-15 涉及 2月、3月、4月，共3个月） */
    private int countMonthsInRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) return 0;
        YearMonth ymStart = YearMonth.from(start);
        YearMonth ymEnd = YearMonth.from(end);
        return (int) ChronoUnit.MONTHS.between(ymStart, ymEnd) + 1;
    }

    /** 判断目标年月是否在日期区间内 */
    private boolean isMonthInRange(YearMonth targetYm, LocalDate start, LocalDate end) {
        LocalDate monthStart = targetYm.atDay(1);
        LocalDate monthEnd = targetYm.atEndOfMonth();
        return !start.isAfter(monthEnd) && !end.isBefore(monthStart);
    }

    private void calcCommission(InfraContractDO contract) {
        if (contract.getAmount() != null && contract.getCommissionRate() != null) {
            contract.setCommissionAmount(contract.getAmount().multiply(contract.getCommissionRate()));
        } else {
            contract.setCommissionAmount(BigDecimal.ZERO);
        }
    }

}
