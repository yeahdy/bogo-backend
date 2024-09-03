package com.boardgo.schedule.config;

import com.boardgo.config.log.OutputLog;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;

@Component
public class TriggersListener implements TriggerListener {
    @Override
    public String getName() {
        return TriggersListener.class.getName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        OutputLog.logInfo(String.format("[%-18s][%s]", "triggerFired", trigger.getKey()));
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false; // 작업 실행 여부를 결정. true를 반환하면 작업 실행이 거부됨.
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        OutputLog.logInfo(
                String.format("[%-18s][%s]", "triggerMisfired", trigger.getKey()) // 미발동된 트리거에 대해 호출
                );
    }

    @Override
    public void triggerComplete(
            Trigger trigger,
            JobExecutionContext context,
            Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        OutputLog.logInfo(String.format("[%-18s][%s]", "triggerComplete", trigger.getKey()));
    }
}
