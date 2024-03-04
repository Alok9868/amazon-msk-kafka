package com.example.demo.controller.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer  {

    private static final Logger logger= LoggerFactory.getLogger(KafkaConsumer.class);
    @KafkaListener(topics = "example8",groupId = "demo-consumer-group" ,containerFactory = "containerFactory")
    public void consume(@Payload List<String> messages, Acknowledgment acknowledgment)
    {
         acknowledgment.acknowledge();
    }


}
