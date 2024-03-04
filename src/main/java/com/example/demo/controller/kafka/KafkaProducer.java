package com.example.demo.controller.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger LOGGER= LoggerFactory.getLogger(KafkaProducer.class);
        
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    public KafkaProducer(KafkaTemplate<String,Object> kafkaTemplate)
    {
        this.kafkaTemplate=kafkaTemplate;
    }


    public void sendMessage(String message)
    {
        LOGGER.info(String.format("message sent %s",message));
        kafkaTemplate.send("topic-name",message);
    }


}
