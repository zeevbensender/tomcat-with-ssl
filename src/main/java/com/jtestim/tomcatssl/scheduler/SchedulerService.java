package com.jtestim.tomcatssl.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;
import static org.quartz.DateBuilder.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * Created by bensende on 31/10/2018.
 */
@Service
@Import({SchedulerConfig.class})
public class SchedulerService implements ApplicationListener<SchedulerStatusEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
    @Autowired
    private SchedulerFactoryBean schedulerFactory;


    @Override
    public void onApplicationEvent(SchedulerStatusEvent evnt) {
        Trigger trgr;
        String jgroup = evnt.getGroup();
        String jname = evnt.getJobName();
        int intrv = evnt.getIntervalSeconds();
        JobDetail job = newJob(evnt.getJobClass())
                .withIdentity(jname, jgroup)
                .build();

        String expr = evnt.getCronExpression();
        if(StringUtils.isNotEmpty(expr) && intrv < 0){
            trgr = newTrigger().withIdentity(jname + ".trigger", jgroup)
                    .withSchedule(cronSchedule(evnt.getCronExpression())).
                            forJob(jname, jgroup).build();
        }else if(StringUtils.isEmpty(expr) && intrv >= 0){
            trgr = newTrigger()
                    .withIdentity(jname + ".trigger", jgroup)
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(intrv)
                            .repeatForever()).forJob(jname, jgroup)
                    .build();
        }else {
            throw new IllegalStateException("Illegal values for either cron expression or interval");
        }
        try {
            schedulerFactory.getScheduler().scheduleJob(job, trgr);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule " + jname + " job");
        }
    }
}
