package com.example.demo.controller;

import com.example.demo.controller.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {


    @Autowired
    private KafkaProducer kafkaProducer;


    @GetMapping("/get")
    public String saveMessage()
    {
        return "hello";
    }

    @GetMapping ("/sendmsg")
    public ResponseEntity<String> publish(@RequestParam("message") String message)
    {
        kafkaProducer.sendMessage(message);
        return ResponseEntity.ok("message sent successfully");
    }

}
