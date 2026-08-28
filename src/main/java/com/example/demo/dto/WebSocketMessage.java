package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 消息体（客户端 ↔ 服务端 JSON 字段保持一致即可）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    private String from;
    private String content;

}
