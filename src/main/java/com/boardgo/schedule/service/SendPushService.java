package com.boardgo.schedule.service;

import static com.boardgo.common.constant.TimeConstant.SECOND;
import static com.boardgo.common.constant.TimeConstant.SECOND_10;

import com.boardgo.schedule.JobRunner;
import com.boardgo.schedule.job.SendPushJob;
import com.boardgo.schedule.service.enums.ScheduleJobs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendPushService extends JobRunner {

    private final Scheduler scheduler;
    private final TriggerService triggerService;
    private final JobDetailService jobDetailService;

    @Override
    protected void doRun() {
        sendPush();
    }

    public void sendPush() {
        ScheduleJobs job = ScheduleJobs.INSTANT_SEND;
        JobKey jobKey = JobKey.jobKey(job.name(), job.getJobGroup());

        JobDetail jobDetail = jobDetailService.jobDetailBuilder(jobKey, SendPushJob.class);
        Trigger simpleTrigger = triggerService.simpleTrigger(jobKey, SECOND_10, SECOND);
        schedule(jobDetail, simpleTrigger);
    }

    private void schedule(JobDetail jobDetail, Trigger trigger) {
        try {
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.warn(
                    "Fail scheduler job :: {} , Error :: {}",
                    jobDetail.getKey().getName(),
                    e.getMessage());
        }
    }
}
