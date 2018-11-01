package com.jtestim.tomcatssl.scheduler;

import org.springframework.context.ApplicationEvent;

/**
 * Created by bensende on 01/11/2018.
 */
public class SchedulerStatusEvent extends ApplicationEvent {
    private final boolean enabled;
    private final String cronExpression;
    private final int intervalSeconds;
    private final String group;
    private final Class jobClass;
    private final String jobName;

    public SchedulerStatusEvent(Object source, boolean state, String cronExpression, int intervalSeconds, String group, Class jobClass, String jobName) {
        super(source);
        this.enabled = state;
        this.cronExpression = cronExpression;
        this.intervalSeconds = intervalSeconds;
        this.group = group;
        this.jobClass = jobClass;
        this.jobName = jobName;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public String getGroup() {
        return group;
    }

    public String getJobName() {
        return jobName;
    }

    public Class getJobClass() {
        return jobClass;
    }
}
