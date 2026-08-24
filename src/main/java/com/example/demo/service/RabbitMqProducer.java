package com.example.demo.service;

import com.example.demo.constant.RabbitMqConstants;
import com.example.demo.dto.DemoMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 发送消息到 RabbitMQ。
 */
@Service
@RequiredArgsConstructor
public class RabbitMqProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(DemoMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.EXCHANGE,
                RabbitMqConstants.ROUTING_KEY,
                message
        );
    }
}
