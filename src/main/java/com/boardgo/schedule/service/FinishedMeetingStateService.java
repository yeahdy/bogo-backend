package com.boardgo.schedule.service;

import com.boardgo.schedule.job.FinishedMeetingStateJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinishedMeetingStateService {

    private final Scheduler scheduler;
    private final TriggerService triggerService;

    @PostConstruct
    private void jobProgress() throws SchedulerException {
        updateFinishMeetingState();
    }

    public void updateFinishMeetingState() {
        String description = "[모임 종료] 상태 변경 Job";
        JobKey jobKey = JobKey.jobKey("finishedMeeting", "MeetingState");
        final int intervalInMinutes = 30;

        JobDetail jobDetail = finishedMeetingStateBuild(jobKey, description);
        Trigger simpleTrigger = triggerService.simpleTrigger(jobKey, intervalInMinutes);
        schedule(jobDetail, simpleTrigger);
    }

    private JobDetail finishedMeetingStateBuild(JobKey jobKey, final String description) {
        return JobBuilder.newJob(FinishedMeetingStateJob.class)
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withDescription(description)
                .build();
    }

    private void schedule(JobDetail jobDetail, Trigger lastTrigger) {
        try {
            scheduler.start();
            scheduler.scheduleJob(jobDetail, lastTrigger);
        } catch (SchedulerException e) {
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.setRefireImmediately(true);
        }
    }
}
