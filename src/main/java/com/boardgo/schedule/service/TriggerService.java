package com.boardgo.schedule.service;

import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

@Service
public class TriggerService {
    public Trigger simpleTrigger(final JobKey jobKey, final int intervalInMinutes) {
        return TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(jobKey.getName())
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMinutes(intervalInMinutes)
                                .repeatForever())
                .build();
    }
}
