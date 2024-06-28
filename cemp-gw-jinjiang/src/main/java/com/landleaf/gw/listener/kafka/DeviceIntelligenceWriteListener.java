package com.landleaf.gw.listener.kafka;

import cn.hutool.json.JSONUtil;
import com.landleaf.gw.conf.JjConstance;
import com.landleaf.gw.domain.dto.DeviceControlDTO;
import com.landleaf.gw.domain.dto.DeviceIntelligenceControlDTO;
import com.landleaf.gw.service.JjRemoteService;
import com.landleaf.kafka.conf.TopicDefineConst;
import com.landleaf.kafka.receive.BaseKafkaListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeviceIntelligenceWriteListener extends BaseKafkaListener {

    @Resource
    private JjRemoteService jjRemoteServiceImpl;

    @KafkaListener(id = "consumer-in-1", idIsGroup = false, groupId = "consumer-in-0", topics = TopicDefineConst.JJ_DEVICE_INTELLIGENCE_WRITE_TOPIC + JjConstance.BIZ_PROJECT_ID)
    public void listen(String in) {
        log.info("Receive intelligence control cmd from platform, content is : {}", in);
        DeviceIntelligenceControlDTO cmd = JSONUtil.toBean(JSONUtil.parseObj(in), DeviceIntelligenceControlDTO.class);
        if (null == cmd.getTs() || cmd.getTs() - System.currentTimeMillis() >= 5 * 60 * 1000L) {
            return;
        }
        jjRemoteServiceImpl.writeIntelligenceCmd(cmd, 0);
    }
}
