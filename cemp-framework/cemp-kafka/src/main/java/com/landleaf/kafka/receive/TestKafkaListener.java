package com.landleaf.kafka.receive;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestKafkaListener extends  BaseKafkaListener{

//    @KafkaListener(id = "consumer-in-0", groupId = "group-test", idIsGroup = false, topics = "test-topic")
//    public void listen(String in) {
//        System.out.println("=============" + in);
//    }
}
