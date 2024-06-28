package com.landleaf.gw.conf;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QuartzConfig {

    @Bean("piJob")
    public JobDetail helloJobDetail() {
        return JobBuilder.newJob(PIJob.class)
                .withIdentity("DateTimeJob")
                .usingJobData("msg", "Hello Quartz")
                .storeDurably()//即使没有Trigger关联时，也不需要删除该JobDetail
                .build();
    }

    @Bean
    public Trigger printTimeJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 */1 * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(helloJobDetail())
                .withIdentity("quartzTaskService")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
