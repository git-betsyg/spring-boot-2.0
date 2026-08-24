package com.example.demo.listener;

import com.example.demo.constant.RabbitMqConstants;
import com.example.demo.dto.DemoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费 {@link RabbitMqConstants#QUEUE} 中的演示消息。
 */
@Slf4j
@Component
public class DemoMessageListener {

    @RabbitListener(queues = RabbitMqConstants.QUEUE)
    public void onMessage(DemoMessage message) {
        log.info("Received demo message: {}", message.getContent());
    }
}
