package com.example.demo.security;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记录每个 WebSocket 会话的 token 过期时间，供入站 / 出站鉴权共用。
 */
@Component
public class WebSocketSessionTokenStore {

    private final ConcurrentHashMap<String, Instant> expiresAtBySession = new ConcurrentHashMap<>();

    /** CONNECT 成功后绑定 sessionId → token 过期时间 */
    public void bind(String sessionId, Instant expiresAt) {
        if (sessionId != null && expiresAt != null) {
            expiresAtBySession.put(sessionId, expiresAt);
        }
    }

    public void unbind(String sessionId) {
        expiresAtBySession.remove(sessionId);
    }

    public boolean isExpired(String sessionId) {
        Instant expiresAt = expiresAtBySession.get(sessionId);
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        unbind(event.getSessionId());
    }
}
