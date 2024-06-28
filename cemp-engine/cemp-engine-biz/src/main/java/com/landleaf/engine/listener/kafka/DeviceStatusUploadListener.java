package com.landleaf.engine.listener.kafka;

import com.alibaba.fastjson2.JSONObject;
import com.landleaf.engine.context.RuleContext;
import com.landleaf.kafka.receive.BaseKafkaListener;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DeviceStatusUploadListener extends BaseKafkaListener {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RuleContext ruleContext;

    @KafkaListener(clientIdPrefix = "${spring.kafka.consumer-id}", idIsGroup = false, groupId = "${spring.kafka.consumer-group}", topicPattern = "device_status_upload_topic_.*", concurrency = "8")
    public void listen(String in) {
        log.info("Receive device status update info from platform, content is : {}", in);
        try {

            JSONObject obj = JSONObject.parseObject(in);
            // 解析消息，处理设备信息
            String gateId = obj.getString("gateId");
            String pkId = obj.getString("pkId");
            String sourceDevId = obj.getString("sourceDevId");

//            long time = obj.getLong("time");

            // 通过gateId, pkId和sourceDevId获取bizDeviceId;
            Object val = redisUtils.hget(KeyConstance.OUTER_DEVICE_RELATION, String.format(KeyConstance.OUTER_DEVICE_KEY, gateId, pkId, sourceDevId));
            String bizDeviceId = null;
            if (null != val) {
                bizDeviceId = String.valueOf(val);
            }
            if (!StringUtils.hasText(bizDeviceId)) {
                // 直接return；gg了
                return;
            }
            // 判断是否有对应的id的信息
            ruleContext.executeRule(bizDeviceId, obj);
        } catch (Exception e) {
            log.error("处理下消息失败。", e);
        }
    }
}
