package com.example.demo.security;

import com.example.demo.enums.SecurityExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * 服务端推送前检查 token 是否过期。
 * 过期则用 STOMP ERROR 帧返回 {@code success: false} 的 ResponseVo，并丢弃原推送内容。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketTokenOutboundInterceptor implements ChannelInterceptor {

    private final WebSocketSessionTokenStore tokenStore;
    private final StompAuthErrorMessages stompAuthErrorMessages;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
        if (accessor == null || accessor.getMessageType() != SimpMessageType.MESSAGE) {
            return message;
        }

        String sessionId = accessor.getSessionId();
        if (sessionId == null || !tokenStore.isExpired(sessionId)) {
            return message;
        }

        log.info("WebSocket token expired on outbound, sessionId={}", sessionId);
        return stompAuthErrorMessages.create(SecurityExceptionCode.UNAUTHORIZED, sessionId);
    }
}
