package com.example.demo.listener;

import com.example.demo.dto.WebSocketMessage;
import com.example.demo.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费本实例 SSE 队列，向本地 {@link SseEmitterService} 订阅者广播。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseMessageListener {

    private final SseEmitterService sseEmitterService;

    @RabbitListener(queues = "#{sseInstanceQueue.name}")
    public void onSseMessage(WebSocketMessage message) {
        log.debug("SSE via RabbitMQ: {}", message);
        sseEmitterService.broadcast(message);
    }
}
