package com.example.demo.controller;

import com.example.demo.dto.DemoMessage;
import com.example.demo.service.RabbitMqProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * RabbitMQ 发送演示接口。
 */
@RestController
@RequiredArgsConstructor
public class RabbitMqDemoController {

    private final RabbitMqProducer rabbitMqProducer;

    @PostMapping("/mq/demo")
    public void send(@RequestBody DemoMessage message) {
        rabbitMqProducer.send(message);
    }
}
