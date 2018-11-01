package com.jtestim.tomcatssl.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by bensende on 31/10/2018.
 */
@Component
public class HelloJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(HelloJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> I'm the most simple job");
    }
}
