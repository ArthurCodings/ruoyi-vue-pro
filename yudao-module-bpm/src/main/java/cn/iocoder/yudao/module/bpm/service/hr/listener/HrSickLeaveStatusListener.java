package cn.iocoder.yudao.module.bpm.service.hr.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请病假流程审批完成监听器
 * 流程标识Key：hr_sick_leave
 * 审批通过后自动将请假区间内的考勤状态更新为病假（status=6）
 */
@Component
@Slf4j
public class HrSickLeaveStatusListener extends BpmProcessInstanceStatusEventListener
        implements ApplicationContextAware {

    /** BPM流程实例状态：审批通过 */
    private static final int STATUS_APPROVE = 2;

    @Resource
    private HistoryService historyService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.applicationContext = ctx;
    }

    @Override
    protected String getProcessDefinitionKey() {
        return "hr_sick_leave";
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        // 只处理审批通过的流程
        if (event.getStatus() == null || event.getStatus() != STATUS_APPROVE) return;

        String processInstanceId = event.getId();
        try {
            // 获取流程发起人ID
            HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (instance == null) {
                log.warn("[HrSickLeaveStatusListener] 流程实例不存在: {}", processInstanceId);
                return;
            }
            Long userId = parseLong(instance.getStartUserId());
            if (userId == null) return;

            // 获取表单变量
            Map<String, Object> variables = getProcessVariables(processInstanceId);
            String leaveStartDate = (String) variables.get("leaveStartDate");
            String leaveEndDate   = (String) variables.get("leaveEndDate");
            if (leaveStartDate == null || leaveEndDate == null) {
                log.warn("[HrSickLeaveStatusListener] 流程实例 {} 缺少请假日期变量", processInstanceId);
                return;
            }

            // 调用考勤服务更新状态（status=6=病假，leaveType=1）
            getAttendanceService().updateLeaveStatus(userId, leaveStartDate, leaveEndDate, 6, 1, processInstanceId);
            log.info("[HrSickLeaveStatusListener] userId={} 病假 {}-{} 考勤已更新", userId, leaveStartDate, leaveEndDate);
        } catch (Exception e) {
            log.error("[HrSickLeaveStatusListener] 处理流程 {} 失败", processInstanceId, e);
        }
    }

    private Map<String, Object> getProcessVariables(String processInstanceId) {
        List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).list();
        return vars.stream().collect(Collectors.toMap(
                HistoricVariableInstance::getVariableName,
                v -> v.getValue() != null ? v.getValue() : "",
                (a, b) -> a));
    }

    private Long parseLong(String value) {
        if (value == null) return null;
        try { return Long.parseLong(value.trim()); } catch (NumberFormatException e) { return null; }
    }

    private cn.iocoder.yudao.module.system.service.hr.attendance.AttendanceRecordService getAttendanceService() {
        return applicationContext.getBean(cn.iocoder.yudao.module.system.service.hr.attendance.AttendanceRecordService.class);
    }

}
