package com.example.demo.listener;

import com.example.demo.constant.RabbitMqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.nio.charset.StandardCharsets;

/**
 * WebSocket 连接事件 + STOMP 广播镜像队列消费（日志可在 logs/demo.log 查看，队列可在 RabbitMQ UI 查看）。
 */
@Slf4j
@Component
public class WebSocketEventListener {

    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        log.info("WebSocket connected: {}", event.getMessage());
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        log.info("WebSocket disconnected: sessionId={}", event.getSessionId());
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        log.info("WebSocket subscribe: sessionId={}, destination={}",
                event.getMessage().getHeaders().get("simpSessionId"),
                event.getMessage().getHeaders().get("simpDestination"));
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent event) {
        log.info("WebSocket unsubscribe: sessionId={}",
                event.getMessage().getHeaders().get("simpSessionId"));
    }

    @RabbitListener(queues = RabbitMqConstants.WS_GREETINGS_QUEUE)
    public void onWsGreetings(Message message) {
        logWsTopicMessage(RabbitMqConstants.WS_GREETINGS_ROUTING_KEY, message);
    }

    @RabbitListener(queues = RabbitMqConstants.WS_NOTIFICATIONS_QUEUE)
    public void onWsNotifications(Message message) {
        logWsTopicMessage(RabbitMqConstants.WS_NOTIFICATIONS_ROUTING_KEY, message);
    }

    private void logWsTopicMessage(String routingKey, Message message) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("WS via RabbitMQ [{}]: {}", routingKey, payload);
    }
}