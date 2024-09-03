package com.boardgo.schedule.job;

import com.boardgo.domain.meeting.service.MeetingBatchServiceV1;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class FinishedMeetingStateJob implements Job {

    @Autowired private MeetingBatchServiceV1 meetingBatchServiceV1;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        meetingBatchServiceV1.updateFinishMeetingState();
    }
}
