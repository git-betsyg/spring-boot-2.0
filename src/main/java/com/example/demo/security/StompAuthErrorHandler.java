package com.example.demo.security;

import com.example.demo.enums.SecurityExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

/**
 * STOMP 入站鉴权失败时，向客户端发送与 REST 相同格式的 ResponseVo JSON（放在 ERROR 帧 body）。
 * 异常分类逻辑与 {@link RestAuthenticationEntryPoint}、{@link RestAccessDeniedHandler} 一致。
 */
@Component
@RequiredArgsConstructor
public class StompAuthErrorHandler extends StompSubProtocolErrorHandler {

    private final StompAuthErrorMessages stompAuthErrorMessages;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        SecurityExceptionCode code = resolveCode(ex);
        return stompAuthErrorMessages.create(code);
    }

    /** 与 REST 侧一致：AccessDeniedException → 403，其余鉴权类异常 → 401 */
    private SecurityExceptionCode resolveCode(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        if (root instanceof AccessDeniedException) {
            return SecurityExceptionCode.FORBIDDEN;
        }
        return SecurityExceptionCode.UNAUTHORIZED;
    }
}
