package com.jtestim.tomcatssl.scheduler;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by bensende on 04/11/2018.
 */
@Service
public class SchedulingEventListener implements ApplicationListener<SchedulerStatusEvent> {
    @Inject
    private SchedulerService schedulerService;
    @Override
    public void onApplicationEvent(SchedulerStatusEvent event) {
        schedulerService.scheduleJob(event);
    }
}
