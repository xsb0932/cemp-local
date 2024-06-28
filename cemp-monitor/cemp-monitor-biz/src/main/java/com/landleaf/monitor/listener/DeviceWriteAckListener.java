package com.landleaf.monitor.listener;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.landleaf.kafka.conf.TopicDefineConst;
import com.landleaf.kafka.receive.BaseKafkaListener;
import com.landleaf.monitor.service.DeviceWriteService;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeviceWriteAckListener extends BaseKafkaListener {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private DeviceWriteService deviceWriteServiceImpl;

    @KafkaListener(id = "consumer-in-0", idIsGroup = false, groupId = "consumer-in-0", topics = TopicDefineConst.DEVICE_WRITE_ACK_TOPIC)
    public void listen(String in) {
        log.info("Receive device control ack msg from gateway, content is : {}", in);
        JSONObject obj = JSONUtil.parseObj(in);
        String msgId = obj.getStr("msgId");
        redisUtils.set(KeyConstance.CMD_EXEC_RESULT + msgId, obj.toString());
        deviceWriteServiceImpl.cmdAck(msgId);
    }
}
