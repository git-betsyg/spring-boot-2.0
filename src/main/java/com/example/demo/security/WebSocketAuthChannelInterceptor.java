package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * STOMP 入站鉴权：
 * <ol>
 *   <li>CONNECT：校验 JWT，绑定用户，记录 token 过期时间</li>
 *   <li>后续帧：检查已登录、token 未过期、SUBSCRIBE / SEND 目标合法</li>
 * </ol>
 * 出站推送时的过期检查见 {@link WebSocketTokenOutboundInterceptor}。
 * 客户端在 STOMP CONNECT 时携带：{@code Authorization: Bearer &lt;access_token&gt;}
 */
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final WebSocketSessionTokenStore tokenStore;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor);
            if (token == null) {
                throw new AuthenticationCredentialsNotFoundException("WebSocket CONNECT missing JWT");
            }
            Jwt jwt;
            try {
                jwt = jwtDecoder.decode(token);
            } catch (JwtException ex) {
                throw new AuthenticationCredentialsNotFoundException("Invalid JWT", ex);
            }
            accessor.setUser(jwtAuthenticationConverter.convert(jwt));
            tokenStore.bind(accessor.getSessionId(), jwt.getExpiresAt());
            return message;
        }

        if (accessor.getUser() == null) {
            throw new AuthenticationCredentialsNotFoundException("Unauthenticated WebSocket session");
        }

        assertTokenNotExpired(accessor);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())
                && !canSubscribe(accessor.getUser(), accessor.getDestination())) {
            throw new AccessDeniedException("Not allowed to subscribe: " + accessor.getDestination());
        }

        if (StompCommand.SEND.equals(accessor.getCommand())
                && !canSend(accessor.getUser(), accessor.getDestination())) {
            throw new AccessDeniedException("Not allowed to send: " + accessor.getDestination());
        }

        return message;
    }

    /** 每个入站帧检查 token 是否过期 */
    private void assertTokenNotExpired(StompHeaderAccessor accessor) {
        if (tokenStore.isExpired(accessor.getSessionId())) {
            throw new AuthenticationCredentialsNotFoundException("Token expired, please reconnect");
        }
    }

    /** SEND 授权：按 application destination 决定谁能发送 */
    private boolean canSend(Principal user, String destination) {
        if (destination == null || !destination.startsWith("/app/")) {
            return false;
        }
        if ("/app/hello".equals(destination)) {
            return hasAuthority(user, "ROLE_ADMIN");
        }
        return false;
    }

    /** SUBSCRIBE 授权：按 destination 前缀决定谁能订阅 */
    private boolean canSubscribe(Principal user, String destination) {
        if (destination == null) {
            return false;
        }
        // 禁止直接订阅内部 queue，防止越权
        if (destination.startsWith("/queue/")) {
            return false;
        }
        // 管理员频道
        if (destination.startsWith("/topic/admin")) {
            return hasAuthority(user, "ROLE_ADMIN");
        }
        // 公开 topic、用户私有通道
        return destination.startsWith("/topic/") || destination.startsWith("/user/");
    }

    private boolean hasAuthority(Principal user, String authority) {
        if (!(user instanceof Authentication)) {
            return false;
        }
        return ((Authentication) user).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader(AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

}
