package com.landleaf.gw.conf;

import com.landleaf.gw.service.PinnengerRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PIJob extends QuartzJobBean {

    @Autowired
    PinnengerRemoteService pinnengerRemoteService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        pinnengerRemoteService.getDevRealKpi(false);
    }
}
