package com.boardgo.schedule.service;

import static com.boardgo.schedule.service.enums.ScheduleJobs.getScheduleJobs;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.stereotype.Service;

@Service
public class JobDetailService {
    public JobDetail jobDetailBuilder(final JobKey jobKey, final Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withDescription(getScheduleJobs(jobKey.getName()).getDescription())
                .build();
    }
}
