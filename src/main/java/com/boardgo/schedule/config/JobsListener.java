package com.boardgo.schedule.config;

import com.boardgo.config.log.OutputLog;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

@Component
public class JobsListener implements JobListener {
    @Override
    public String getName() {
        return "JobsListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        OutputLog.logInfo(
                String.format(
                        "[%-18s][%s] 작업시작",
                        "jobToBeExecuted", context.getJobDetail().getKey().toString()));
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        OutputLog.logInfo(
                String.format(
                        "[%-18s][%s] 작업중단",
                        "jobExecutionVetoed", context.getJobDetail().getKey().toString()));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        OutputLog.logInfo(
                String.format(
                        "[%-18s][%s] 작업완료",
                        "jobWasExecuted", context.getJobDetail().getKey().toString()));
    }
}
