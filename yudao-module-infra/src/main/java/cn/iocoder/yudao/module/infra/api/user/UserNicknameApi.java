package cn.iocoder.yudao.module.infra.api.user;

/**
 * 用户昵称 API 接口（供 system 模块实现，避免 infra 依赖 system 造成循环依赖）
 *
 * @author 芋道源码
 */
public interface UserNicknameApi {

    /**
     * 根据用户 ID 获取昵称
     *
     * @param userId 用户 ID
     * @return 昵称，不存在时返回 null
     */
    String getNickname(Long userId);

}
