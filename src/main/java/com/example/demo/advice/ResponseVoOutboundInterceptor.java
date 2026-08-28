package com.example.demo.advice;

import com.example.demo.common.ResponseVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * WebSocket 出站统一包装：推送前，将 payload 包成 {@link ResponseVo}。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseVoOutboundInterceptor implements ChannelInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
        if (accessor == null
                || accessor.getMessageType() != SimpMessageType.MESSAGE
                || accessor.getDestination() == null
                || !accessor.getDestination().startsWith("/topic/")) {
            return message;
        }

        Object payload = message.getPayload();
        Object wrapped = wrap(payload);
        return wrapped == payload ? message : MessageBuilder.createMessage(wrapped, message.getHeaders());
    }

    private Object wrap(Object payload) {
        if (payload instanceof ResponseVo) {
            return payload;
        }
        try {
            if (payload instanceof byte[]) {
                byte[] bytes = (byte[]) payload;
                return wrapJson(objectMapper.readTree(bytes), bytes, true);
            }
            if (payload instanceof String) {
                String text = (String) payload;
                return wrapJson(objectMapper.readTree(text), text, false);
            }
            return new ResponseVo(payload);
        } catch (Exception ex) {
            log.warn("WebSocket outbound wrap skipped: {}", ex.getMessage());
            return payload;
        }
    }

    private Object wrapJson(JsonNode node, Object original, boolean asBytes) throws Exception {
        if (node.has("success")) {
            return original;
        }
        ResponseVo vo = new ResponseVo(objectMapper.treeToValue(node, Object.class));
        return asBytes ? objectMapper.writeValueAsBytes(vo) : objectMapper.writeValueAsString(vo);
    }
}
