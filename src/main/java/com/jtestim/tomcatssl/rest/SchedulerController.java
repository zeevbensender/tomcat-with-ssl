package com.jtestim.tomcatssl.rest;

import com.jtestim.tomcatssl.scheduler.HelloJob;
import com.jtestim.tomcatssl.scheduler.SchedulerService;
import com.jtestim.tomcatssl.scheduler.SchedulerStatusEvent;
import com.jtestim.tomcatssl.util.SchedulingException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * Created by bensende on 04/11/2018.
 */
@RestController
public class SchedulerController {
    @Inject
    private ApplicationContext applicationContext;

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerController.class);
    @Inject
    private SchedulerService schedulerService;

    @GetMapping("/scheduled")
    public String schedule(HttpServletRequest request) {
        try {
            String result = schedulerService.
                    getAllScheduledTriggers().
                    stream().
                    map(tr -> tr.getJobKey().getGroup() + ":" + tr.getJobKey().getName()).
                    collect(Collectors.joining("; ", "Scheduled jobs: [", "]"));
            return result;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("disable/{group}/{name}")
    public String disableJob(@PathVariable String group, @PathVariable String name){
        try {
            schedulerService.unscheduleJob(group, name);
            return "Unscheduled job associated with trigger " + group + ":" + name;
        } catch (SchedulingException e) {
            LOG.error(e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("schedule/{group}/{name}/{interval}")
    public String schedulePeriodicJob(@PathVariable String group, @PathVariable String name, @PathVariable int interval){
        try {
            SchedulerStatusEvent event = new SchedulerStatusEvent(
                    applicationContext,
                    true,
                    null,
                    interval,
                    group,
                    HelloJob.class,
                    name);
            schedulerService.scheduleJob(event);
            return "Scheduled job associated with trigger " + group + ":" + name + " with interval " + interval;
        } catch (SchedulingException e) {
            LOG.error(e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }


}
