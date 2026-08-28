package com.example.demo.service;

import com.example.demo.common.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE 连接管理：维护本实例订阅者，并向本地在线客户端广播事件。
 * <p>
 * 跨实例推送由 {@link com.example.demo.listener.SseMessageListener} 经 RabbitMQ 触发。
 */
@Slf4j
@Service
public class SseEmitterService {

    @Value("${jwt.access-token-expiry:900}")
    private long accessTokenExpirySeconds;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        long timeoutMs = resolveEmitterTimeout();
        SseEmitter emitter = new SseEmitter(timeoutMs);
        emitters.add(emitter);
        removeOnFinish(emitter);

        try {
            emitter.send(SseEmitter.event().name("connected").data(new ResponseVo("ok")));
        } catch (IOException ex) {
            emitters.remove(emitter);
            emitter.completeWithError(ex);
        }
        log.debug("SSE subscribed, online={}, timeoutMs={}", emitters.size(), timeoutMs);
        return emitter;
    }

    /** 使用当前 JWT 的剩余有效期（毫秒）作为单连接超时 */
    private long resolveEmitterTimeout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            Instant expiresAt = ((JwtAuthenticationToken) authentication).getToken().getExpiresAt();
            if (expiresAt != null) {
                long remainingMs = Duration.between(Instant.now(), expiresAt).toMillis();
                return Math.max(remainingMs, 1L);
            }
        }
        log.warn("SSE subscribe without JWT expiresAt, fallback to configured access-token-expiry");
        return accessTokenExpirySeconds * 1000L;
    }

    public void broadcast(Object data) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(toResponseVo(data), MediaType.APPLICATION_JSON));
            } catch (IOException ex) {
                emitters.remove(emitter);
                emitter.completeWithError(ex);
            }
        }
        log.debug("SSE broadcast to {}, data={}", emitters.size(), data);
    }

    private static Object toResponseVo(Object data) {
        return data instanceof ResponseVo ? data : new ResponseVo(data);
    }

    private void removeOnFinish(SseEmitter emitter) {
        Runnable cleanup = () -> {
            emitters.remove(emitter);
            log.debug("SSE disconnected, online={}", emitters.size());
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());
    }
}
