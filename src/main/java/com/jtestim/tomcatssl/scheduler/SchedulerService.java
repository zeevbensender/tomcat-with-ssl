package com.jtestim.tomcatssl.scheduler;

import com.jtestim.tomcatssl.util.SchedulingException;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by bensende on 31/10/2018.
 */
@Service
@Import({SchedulerConfig.class})
public class SchedulerService {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
    @Autowired
    private SchedulerFactoryBean schedulerFactory;
    private Scheduler sched;

    @PostConstruct
    private void init(){
        sched = schedulerFactory.getScheduler();
    }


    public void scheduleJob(SchedulerStatusEvent event) throws SchedulingException {
        String jgroup = event.getGroup();
        String jname = event.getJobName();
        JobDetail job = getScheduledJob(jgroup, jname);


        Trigger trigger;
        if (StringUtils.isNotEmpty(event.getCronExpression()) && event.getIntervalSeconds() < 0) {
            trigger = createCronTrigger(event);
        } else if (StringUtils.isEmpty(event.getCronExpression()) && event.getIntervalSeconds() >= 0) {
            trigger = createPeriodicTrigger(event);
        } else {
            throw new IllegalStateException("Illegal values for either cron expression or interval");
        }

        if(job == null){
            job = newJob(event.getJobClass())
                    .withIdentity(jname, jgroup)
                    .build();
            scheduleJob(job, trigger);
        }else {
            rescheduleJob(trigger);
        }
    }

    private CronTrigger createCronTrigger(SchedulerStatusEvent event) {
        return newTrigger().withIdentity(event.getJobName() + ".trigger", event.getGroup())
                .withSchedule(cronSchedule(event.getCronExpression())).
                        forJob(event.getJobName(), event.getGroup()).build();
    }


    private SimpleTrigger createPeriodicTrigger(SchedulerStatusEvent event) {
        return newTrigger()
                .withIdentity(event.getJobName() + ".trigger", event.getGroup())
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(event.getIntervalSeconds())
                        .repeatForever()).forJob(event.getJobName(), event.getGroup())
                .build();
    }

    public JobDetail getScheduledJob(String group, String name) throws SchedulingException {
        try {
            return sched.getJobDetail(new JobKey(name, group));
        } catch (SchedulerException e) {
            throw new SchedulingException("Failed to fetch scheduled job", e);
        }
    }

    public List<Trigger> getAllScheduledTriggers() throws SchedulerException{
        List<Trigger> runningTriggers = new ArrayList<>();
        Scheduler scheduler = sched;
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();

                //get job's trigger
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);


                runningTriggers.addAll(triggers);

                Date nextFireTime = triggers.get(0).getNextFireTime();

                LOG.error("[jobName] : " + jobName + " [groupName] : "+ jobGroup + " - " + nextFireTime);
            }
        }
        return runningTriggers;
    }

    public void unscheduleJob(String group, String name)throws SchedulingException{
        try{
            JobDetail jd = getScheduledJob(name, group);
            if(jd != null) {
                sched.deleteJob(jd.getKey());
            }
            LOG.error("Job {}.{} doesn't exist", name, group);
        } catch (SchedulerException e) {
            throw new SchedulingException("Failed to unschedule job", e);
        }
    }

    private void scheduleJob(JobDetail job, Trigger trigger) throws SchedulingException {
        try {
            sched.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
            throw new SchedulingException("Failed to schedule job", e);
        }
    }

    private void rescheduleJob(Trigger trigger) throws SchedulingException {
        try {
            sched.rescheduleJob(trigger.getKey(), trigger);
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
            throw new SchedulingException("Failed to reschedule job", e);
        }
    }



}
