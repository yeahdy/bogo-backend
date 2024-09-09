package com.boardgo.schedule.job;

import com.boardgo.domain.meeting.service.MeetingBatchService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class FinishedMeetingStateJob implements Job {

    @Autowired private MeetingBatchService meetingBatchService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        meetingBatchService.updateFinishMeetingState();
    }
}
