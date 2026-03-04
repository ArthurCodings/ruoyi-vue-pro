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
 * 补打卡流程审批完成监听器
 * 流程标识Key：hr_clock_supplement
 * 审批通过后自动补写当天打卡记录，并重新计算考勤状态
 */
@Component
@Slf4j
public class HrClockSupplementStatusListener extends BpmProcessInstanceStatusEventListener
        implements ApplicationContextAware {

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
        return "hr_clock_supplement";
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        if (event.getStatus() == null || event.getStatus() != STATUS_APPROVE) return;

        String processInstanceId = event.getId();
        try {
            HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            if (instance == null) return;
            Long userId = parseLong(instance.getStartUserId());
            if (userId == null) return;

            Map<String, Object> variables = getProcessVariables(processInstanceId);
            String supplementDate = (String) variables.get("supplementDate");
            String clockType      = String.valueOf(variables.getOrDefault("clockType", "3"));
            String clockInTime    = (String) variables.get("clockInTime");
            String clockOutTime   = (String) variables.get("clockOutTime");

            if (supplementDate == null) {
                log.warn("[HrClockSupplementStatusListener] 流程实例 {} 缺少补打卡日期变量", processInstanceId);
                return;
            }

            getAttendanceService().supplementClock(userId, supplementDate, clockType,
                    clockInTime, clockOutTime, processInstanceId);
            log.info("[HrClockSupplementStatusListener] userId={} 补打卡 {} 类型={} 已处理", userId, supplementDate, clockType);
        } catch (Exception e) {
            log.error("[HrClockSupplementStatusListener] 处理流程 {} 失败", processInstanceId, e);
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
