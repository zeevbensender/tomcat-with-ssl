package com.jtestim.tomcatssl.scheduler;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.inject.Inject;

/**
 * Created by bensende on 31/10/2018.
 */
@Configuration
public class SchedulerConfig {

    @Inject
    private ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBean quartzScheduler() {
        SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();
//        quartzScheduler.setTransactionManager(transactionManager);
        quartzScheduler.setOverwriteExistingJobs(true);
        quartzScheduler.setSchedulerName("tutorial-scheduler");
        // custom job factory of spring with DI support for @Autowired!
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
/*
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
*/
        quartzScheduler.setJobFactory(jobFactory);


        // quartzScheduler.setQuartzProperties(quartzProperties());

     /*   Trigger[] triggers = { simpleTriggerFactoryBean().getObject() };*/
     /*   quartzScheduler.setTriggers(triggers);*/
        return quartzScheduler;
    }



}
