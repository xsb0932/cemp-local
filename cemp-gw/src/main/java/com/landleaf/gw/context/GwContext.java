package com.landleaf.gw.context;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.landleaf.script.GwConfigBO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang
 */
@Data
@Slf4j
@Component
public class GwContext {
    /**
     * up k:topic v:description
     */
    private static final Map<String, String> UP_TOPICS = new HashMap<>(16);
    /**
     * down k:topic v:description
     */
    private static final Map<String, String> DOWN_TOPICS = new HashMap<>(16);
    /**
     * handle up js
     */
    private String upJs;
    /**
     * handle down js
     */
    private String downJs;
    /**
     * 网关业务id
     */
    @Value("${cemp.gateway-biz-id:demo}")
    private String bizId;

    public Map<String, String> getUpTopics() {
        return UP_TOPICS;
    }

    public Map<String, String> getDownTopics() {
        return DOWN_TOPICS;
    }

    public void init() {
        String configFile = "/app/" + bizId + ".json";
//        String configFile = "GW00000001.json";
        if (FileUtil.exist(configFile)) {
            log.info("read config json {}", configFile);
            String data = FileUtil.readString(configFile, StandardCharsets.UTF_8);
            GwConfigBO configBO = JSONUtil.toBean(data, GwConfigBO.class);
            if (null != configBO) {
                if (MapUtil.isNotEmpty(configBO.getUpTopics())) {
                    UP_TOPICS.putAll(configBO.getUpTopics());
                }
                if (MapUtil.isNotEmpty(configBO.getDownTopics())) {
                    DOWN_TOPICS.putAll(configBO.getDownTopics());
                }
                upJs = configBO.getUpJs();
                downJs = configBO.getDownJs();
            }
        }
    }
}
