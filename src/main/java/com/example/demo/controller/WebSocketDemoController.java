package com.example.demo.controller;

import com.example.demo.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * WebSocket 演示：STOMP 消息处理 + REST 主动推送。
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketDemoController {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 客户端发送到 {@code /app/hello}，服务端广播到 {@code /topic/greetings}。
     * <p>
     * 示例 JSON：{@code {"from":"alice","content":"hi"}}
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public WebSocketMessage hello(WebSocketMessage message, Principal principal) {
        String from = principal != null ? principal.getName() : message.getFrom();
        WebSocketMessage reply = new WebSocketMessage(from, "Hello, " + message.getContent() + "!");
        log.info("WS /app/hello -> /topic/greetings: {}", reply);
        return reply;
    }
    /**
     * REST 主动推送（例如定时任务、MQ 消费后通知前端）。
     * 订阅 {@code /topic/notifications} 的客户端都会收到。
     */
    @PostMapping("/ws/push")
    @ResponseBody
    public WebSocketMessage push(@RequestBody WebSocketMessage message) {
        log.info("WS REST push -> /topic/notifications: {}", message);
        messagingTemplate.convertAndSend("/topic/notifications", message);
        return message;    }

}
