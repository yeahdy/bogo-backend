package com.boardgo.schedule;

import jakarta.annotation.PostConstruct;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public abstract class JobRunner {

    @PostConstruct
    private void jobProgress() throws SchedulerException {
        doRun();
    }

    protected abstract void doRun();
}
