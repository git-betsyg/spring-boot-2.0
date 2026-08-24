package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RabbitMQ 演示消息体。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoMessage {

    private String content;
}
