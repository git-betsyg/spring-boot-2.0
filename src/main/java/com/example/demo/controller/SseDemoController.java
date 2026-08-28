package com.example.demo.controller;

import com.example.demo.dto.WebSocketMessage;
import com.example.demo.service.SseEmitterService;
import com.example.demo.service.SseMqProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 演示：长连接订阅 + REST 主动推送（类似 {@code /ws/push}）。
 * <p>
 * 推送经 RabbitMQ Fanout 广播，多实例部署时各节点推送给本地 SSE 连接。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SseDemoController {

    private final SseEmitterService sseEmitterService;
    private final SseMqProducer sseMqProducer;

    /**
     * 建立 SSE 连接，服务端持续推送 {@code event: message}。
     * <p>
     * 浏览器原生 {@code EventSource} 无法带 Authorization 头，演示页用 fetch 读取流。
     */
    @GetMapping(value = "/sse/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseEmitterService.subscribe();
    }

    /**
     * REST 主动推送，经 MQ 广播到所有实例，各实例本地 SSE 客户端都会收到。
     */
    @PostMapping("/sse/push")
    public WebSocketMessage push(@RequestBody WebSocketMessage message) {
        log.info("SSE REST push via MQ: {}", message);
        sseMqProducer.publish(message);
        return message;
    }
}
