package com.landleaf.gw.task;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.landleaf.gw.conf.AirConditionPlatformConf;
import com.landleaf.gw.service.JjRemoteService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.requests.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 锦江空调需要自己到人家平台去定时读取状态
 */
@Configuration
@EnableConfigurationProperties(AirConditionPlatformConf.class)
@Slf4j
public class AirConditionTask {

    @Resource
    public AirConditionPlatformConf airConditionPlatformConf;

    @Resource
    private JjRemoteService jjRemoteServiceImpl;

    /**
     * 从第三方获取设备列表信息和设备的当前状态
     *
     * @return
     */
    @Scheduled(cron = "0 * *  * * ?")
    public boolean getDeviceList() {
        if (!airConditionPlatformConf.isEnabled()) {
            return false;
        }
        if (!StringUtils.hasText(jjRemoteServiceImpl.getToken())) {
            if (!jjRemoteServiceImpl.freshToken()) {
                log.error("调用第三方获取token失败");
                return false;
            }
        }

        if (!jjRemoteServiceImpl.getAirConditionList(0)) {
            return false;
        }
        return true;
    }
}