package com.example.demo.constant;

/**
 * RabbitMQ 演示用交换机 / 队列 / 路由键名称。
 */
public final class RabbitMqConstants {

    public static final String EXCHANGE = "demo.direct";

    public static final String QUEUE = "demo.queue";

    public static final String ROUTING_KEY = "demo.message";

    /** 文章 MySQL → ES 异步同步队列 */
    public static final String ES_ARTICLE_SYNC_QUEUE = "es.article.sync";

    public static final String ES_ARTICLE_SYNC_ROUTING_KEY = "es.article.sync";

    /** RabbitMQ STOMP Relay：{@code /topic/greetings} 对应的 topic routing key（无 {@code /topic/} 前缀） */
    public static final String TOPIC_EXCHANGE = "amq.topic";

    public static final String WS_GREETINGS_QUEUE = "ws.greetings";

    public static final String WS_NOTIFICATIONS_QUEUE = "ws.notifications";

    public static final String WS_GREETINGS_ROUTING_KEY = "greetings";

    public static final String WS_NOTIFICATIONS_ROUTING_KEY = "notifications";

    /** SSE 分布式广播：Fanout 交换机，各实例绑定独占队列后本地推送给 SSE 客户端 */
    public static final String SSE_FANOUT_EXCHANGE = "sse.broadcast";

    private RabbitMqConstants() {
    }
}
