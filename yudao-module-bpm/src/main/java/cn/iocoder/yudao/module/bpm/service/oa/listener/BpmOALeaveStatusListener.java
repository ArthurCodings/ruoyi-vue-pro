package cn.iocoder.yudao.module.bpm.service.oa.listener;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import cn.iocoder.yudao.module.bpm.service.oa.BpmOALeaveService;
import cn.iocoder.yudao.module.bpm.service.oa.BpmOALeaveServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * OA 请假单的结果的监听器实现类
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class BpmOALeaveStatusListener extends BpmProcessInstanceStatusEventListener {

    @Resource
    private BpmOALeaveService leaveService;

    @Override
    protected String getProcessDefinitionKey() {
        return BpmOALeaveServiceImpl.PROCESS_KEY;
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        Long leaveId = null;
        String businessKey = event.getBusinessKey();
        if (StrUtil.isNotBlank(businessKey)) {
            try {
                leaveId = Long.parseLong(businessKey.trim());
            } catch (NumberFormatException e) {
                log.warn("[onEvent][流程实例({}) businessKey({}) 无法解析为请假单 ID]", event.getId(), businessKey, e);
            }
        }
        // businessKey 为空时（如流程完成时引擎未带回），尝试按流程实例编号反查请假单
        if (leaveId == null && StrUtil.isNotBlank(event.getId())) {
            var leave = leaveService.getLeaveByProcessInstanceId(event.getId());
            if (leave != null) {
                leaveId = leave.getId();
            }
        }
        if (leaveId == null) {
            log.warn("[onEvent][流程实例({}) 无法解析到 OA 请假单，跳过状态更新]", event.getId());
            return;
        }
        leaveService.updateLeaveStatus(leaveId, event.getStatus());
    }

}
