package com.jtestim.tomcatssl.scheduler;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;
import static org.quartz.DateBuilder.*;
import javax.annotation.PostConstruct;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * Created by bensende on 31/10/2018.
 */
@Service
public class SchedulerService {
    @PostConstruct
    public void init() throws SchedulerException {
        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler sched = schedFact.getScheduler();
        sched.start();
        JobDetail job = newJob(HelloJob.class)
                .withIdentity("myJob", "group1")
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(40)
                        .repeatForever())
                .build();
        sched.scheduleJob(job, trigger);

    }

}
