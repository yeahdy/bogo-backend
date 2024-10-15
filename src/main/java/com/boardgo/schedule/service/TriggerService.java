package com.boardgo.schedule.service;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

@Service
public class TriggerService {
    public Trigger simpleTrigger(
            final JobKey jobKey, final int intervalInTime, String intervalTime) {
        return TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(jobKey.getName())
                .withSchedule(simpleScheduleBuilder(intervalInTime, intervalTime).repeatForever())
                .build();
    }

    private SimpleScheduleBuilder simpleScheduleBuilder(
            final int intervalInTime, String intervalTime) {
        return switch (intervalTime) {
            case "SECOND" -> SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(intervalInTime);
            case "MINUTE" -> SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(intervalInTime);
            case "HOUR" -> SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInHours(intervalInTime);
            default -> throw new CustomIllegalArgumentException(
                    "Not invalid intervalTime: " + intervalTime);
        };
    }
}
