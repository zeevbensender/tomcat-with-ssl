package com.jtestim.tomcatssl.scheduler;

import com.jtestim.tomcatssl.util.SchedulingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by bensende on 04/11/2018.
 */
@Service
public class SchedulingEventListener implements ApplicationListener<SchedulerStatusEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulingEventListener.class);
    @Inject
    private SchedulerService schedulerService;
    @Override
    public void onApplicationEvent(SchedulerStatusEvent event) {
        try {

            if (!event.isEnabled()) {
                schedulerService.unscheduleJob(event.getGroup(), event.getJobName());
            }else {
                schedulerService.scheduleJob(event);
            }

        }catch (SchedulingException e){
            LOG.error(e.getMessage(), e);
        }

    }
}
