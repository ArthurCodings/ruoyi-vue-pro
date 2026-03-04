package cn.iocoder.yudao.module.system.dal.mysql.hr.salary;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryConfirmNoticeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalaryConfirmNoticeMapper extends BaseMapperX<SalaryConfirmNoticeDO> {

    default SalaryConfirmNoticeDO selectBySalaryMonthlyId(Long salaryMonthlyId) {
        return selectOne(new LambdaQueryWrapperX<SalaryConfirmNoticeDO>()
                .eq(SalaryConfirmNoticeDO::getSalaryMonthlyId, salaryMonthlyId)
                .orderByDesc(SalaryConfirmNoticeDO::getId)
                .last("LIMIT 1"));
    }

    default List<SalaryConfirmNoticeDO> selectPendingByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<SalaryConfirmNoticeDO>()
                .eq(SalaryConfirmNoticeDO::getUserId, userId)
                .eq(SalaryConfirmNoticeDO::getStatus, 0)
                .orderByDesc(SalaryConfirmNoticeDO::getSendTime));
    }

    default List<SalaryConfirmNoticeDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<SalaryConfirmNoticeDO>()
                .eq(SalaryConfirmNoticeDO::getUserId, userId)
                .orderByDesc(SalaryConfirmNoticeDO::getSendTime));
    }

}
