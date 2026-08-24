package com.example.demo.constant;

/**
 * RabbitMQ 演示用交换机 / 队列 / 路由键名称。
 */
public final class RabbitMqConstants {

    public static final String EXCHANGE = "demo.direct";

    public static final String QUEUE = "demo.queue";

    public static final String ROUTING_KEY = "demo.message";

    private RabbitMqConstants() {
    }
}
