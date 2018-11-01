package com.jtestim.tomcatssl.scheduler;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by bensende on 01/11/2018.
 */
@Service
public class SchedulerConfigService {
    @Inject
    private ApplicationContext applicationContext;

    @PostConstruct
    public void initScheduler(){
        SchedulerStatusEvent cronEvent = new SchedulerStatusEvent(
                applicationContext,
                true,
                "0/15 * * * * ?",
                -1,
                "system",
                CronJob.class,
                "cronJob");

        SchedulerStatusEvent periodicEvent = new SchedulerStatusEvent(
                applicationContext,
                true,
                null,
                25,
                "system",
                HelloJob.class,
                "periodicJob");

        applicationContext.publishEvent(cronEvent);
        applicationContext.publishEvent(periodicEvent);

    }
}
