package com.landleaf.messaging.queue;

import com.alibaba.fastjson2.JSON;
import com.landleaf.messaging.domain.DeviceLastCommunicationInfo;
import com.landleaf.messaging.service.DeviceCtsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class DelayQueueManager implements CommandLineRunner {

    private static DelayQueue<DeviceLastCommunicationInfo> queue = new DelayQueue();

    @Resource
    private DeviceCtsService deviceCtsServiceImpl;

    @Override
    public void run(String... args) throws Exception {
        // 启动delayQueue
        Executors.newSingleThreadExecutor().execute(new Thread(this::executeThread));
    }

    private void executeThread() {
        DeviceLastCommunicationInfo msg = null;
        while (true) {
            try {
                msg = queue.take();
                // 判断超时
                deviceCtsServiceImpl.dealDeviceConnStatus(msg);
            } catch (Exception e) {
                // 记录，并且下一条
                log.error("处理消息延时消息异常， 消息内容为：{}， 异常为：{}", JSON.toJSONString(msg), e);
            }
        }
    }

    public void addQueueMsg(DeviceLastCommunicationInfo info) {
        queue.add(info);
    }
}
