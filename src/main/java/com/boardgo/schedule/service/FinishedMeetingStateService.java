package com.boardgo.schedule.service;

import static com.boardgo.schedule.service.enums.ScheduleJobs.FINISHED_MEETING;

import com.boardgo.schedule.job.FinishedMeetingStateJob;
import com.boardgo.schedule.service.enums.ScheduleJobs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
    private final JobDetailService jobDetailService;

    @PostConstruct
    private void jobProgress() throws SchedulerException {
        updateFinishMeetingState();
    }

    public void updateFinishMeetingState() {
        ScheduleJobs job = FINISHED_MEETING;
        JobKey jobKey = JobKey.jobKey(job.name(), job.getJobGroup());
        final int intervalInMinutes = 30;

        JobDetail jobDetail =
                jobDetailService.jobDetailBuilder(jobKey, FinishedMeetingStateJob.class);
        Trigger simpleTrigger = triggerService.simpleTrigger(jobKey, intervalInMinutes);
        schedule(jobDetail, simpleTrigger);
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
