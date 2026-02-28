package cn.iocoder.yudao.module.system.service.hr.profile;

import cn.iocoder.yudao.module.system.controller.admin.hr.profile.vo.PersonalCenterPanelVO;

public interface PersonalCenterService {

    /**
     * 获取个人中心聚合面板数据
     *
     * @param userId    当前登录用户ID
     * @param yearMonth 目标年月（如 202603），传 null 时默认当月
     * @return 聚合面板数据
     */
    PersonalCenterPanelVO getPanel(Long userId, Integer yearMonth);

}
