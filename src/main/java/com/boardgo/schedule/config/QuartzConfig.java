package com.boardgo.schedule.config;

import java.util.Properties;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final JobsListener jobsListener;
    private final TriggersListener triggersListener;
    private final ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            DataSource dataSource, QuartzProperties quartzProperties) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        // yml
        schedulerFactory.setDataSource(dataSource);
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        schedulerFactory.setQuartzProperties(properties);

        // application start > scheduler auto start after 30s
        schedulerFactory.setAutoStartup(true);
        schedulerFactory.setStartupDelay(3);

        schedulerFactory.setGlobalJobListeners(jobsListener);
        schedulerFactory.setGlobalTriggerListeners(triggersListener);
        schedulerFactory.setOverwriteExistingJobs(true);
        schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);

        // custom job factory of spring with DI support for @Autowired
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactory.setJobFactory(jobFactory);

        return schedulerFactory;
    }
}
