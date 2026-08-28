package com.example.demo.security;

import com.example.demo.common.ResponseVo;
import com.example.demo.enums.SecurityExceptionCode;
import com.example.demo.service.I18nMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 构建 STOMP ERROR 帧，body 为与 REST 相同格式的 {@link ResponseVo} JSON（success: false）。
 */
@Component
@RequiredArgsConstructor
public class StompAuthErrorMessages {

    private final ObjectMapper objectMapper;
    private final I18nMessageService i18nMessageService;

    public Message<byte[]> create(SecurityExceptionCode code) {
        return create(code, null);
    }

    public Message<byte[]> create(SecurityExceptionCode code, String sessionId) {
        try {
            String message = i18nMessageService.getMessage(code.getErrorMessage());
            ResponseVo vo = new ResponseVo(code.getErrorCode(), message);
            String json = objectMapper.writeValueAsString(vo);

            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
            // STOMP message 头仅支持 ASCII；中文放在 JSON body 里（UTF-8）
            accessor.setMessage(String.valueOf(code.getErrorCode()));
            accessor.setNativeHeader("content-type", "application/json;charset=UTF-8");
            if (sessionId != null) {
                accessor.setSessionId(sessionId);
            }
            accessor.setLeaveMutable(true);
            return MessageBuilder.createMessage(json.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to build STOMP ERROR message", ex);
        }
    }
}
