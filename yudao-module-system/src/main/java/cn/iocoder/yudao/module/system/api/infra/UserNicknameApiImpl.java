package cn.iocoder.yudao.module.system.api.infra;

import cn.iocoder.yudao.module.infra.api.user.UserNicknameApi;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 用户昵称 API 实现类（供 infra 合同模块获取负责人姓名快照）
 *
 * @author 芋道源码
 */
@Service
public class UserNicknameApiImpl implements UserNicknameApi {

    @Resource
    private AdminUserService adminUserService;

    @Override
    public String getNickname(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        AdminUserDO user = adminUserService.getUser(userId);
        return user != null ? user.getNickname() : null;
    }

}
