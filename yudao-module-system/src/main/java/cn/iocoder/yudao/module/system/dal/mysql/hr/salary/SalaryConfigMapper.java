package cn.iocoder.yudao.module.system.dal.mysql.hr.salary;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.hr.salary.SalaryConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SalaryConfigMapper extends BaseMapperX<SalaryConfigDO> {

    default SalaryConfigDO selectFirst() {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SalaryConfigDO>()
                .last("LIMIT 1"));
    }

}
