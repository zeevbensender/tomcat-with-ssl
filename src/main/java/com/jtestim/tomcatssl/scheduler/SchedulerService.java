package com.jtestim.tomcatssl.scheduler;

import com.jtestim.tomcatssl.util.SchedulingException;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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


    public void scheduleCronJob(SchedulerStatusEvent evnt) throws SchedulingException {
        String jgroup = evnt.getGroup();
        String jname = evnt.getJobName();
        JobDetail job = newJob(evnt.getJobClass())
                .withIdentity(jname, jgroup)
                .build();

        Trigger trigger = newTrigger().withIdentity(jname + ".trigger", jgroup)
                .withSchedule(cronSchedule(evnt.getCronExpression())).
                        forJob(jname, jgroup).build();
        scheduleJob(job, trigger);
    }

    public void schedulePeriodicJob(SchedulerStatusEvent evnt) throws SchedulingException {
        String jgroup = evnt.getGroup();
        String jname = evnt.getJobName();
        int intrv = evnt.getIntervalSeconds();
        JobDetail job = newJob(evnt.getJobClass())
                .withIdentity(jname, jgroup)
                .build();

        Trigger trigger = newTrigger()
                .withIdentity(jname + ".trigger", jgroup)
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(intrv)
                        .repeatForever()).forJob(jname, jgroup)
                .build();
        scheduleJob(job, trigger);
    }

    public JobDetail getScheduledJob(String group, String name) throws SchedulerException {
        return sched.getJobDetail(new JobKey(group, name));
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



}
