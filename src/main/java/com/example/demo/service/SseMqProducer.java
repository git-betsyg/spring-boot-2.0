package com.example.demo.service;

import com.example.demo.constant.RabbitMqConstants;
import com.example.demo.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 将 SSE 推送事件发布到 RabbitMQ Fanout，由各实例消费后推送给本地 SSE 连接。
 */
@Service
@RequiredArgsConstructor
public class SseMqProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publish(WebSocketMessage message) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.SSE_FANOUT_EXCHANGE, "", message);
    }
}
