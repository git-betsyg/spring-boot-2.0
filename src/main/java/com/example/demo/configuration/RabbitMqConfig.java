package com.example.demo.configuration;

import com.example.demo.constant.RabbitMqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 基础拓扑：Direct Exchange + Queue + Binding。
 * <p>
 * 连接参数见 {@code spring.rabbitmq.*}，JSON 序列化由 {@link MessageConverter} 统一处理。
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RabbitMqConfig {

    @Bean
    public DirectExchange demoExchange() {
        return new DirectExchange(RabbitMqConstants.EXCHANGE, true, false);
    }

    @Bean
    public Queue demoQueue() {
        return QueueBuilder.durable(RabbitMqConstants.QUEUE).build();
    }

    @Bean
    public Binding demoBinding(Queue demoQueue, DirectExchange demoExchange) {
        return BindingBuilder.bind(demoQueue)
                .to(demoExchange)
                .with(RabbitMqConstants.ROUTING_KEY);
    }

    /** WebSocket STOMP 广播镜像队列，启动时自动绑定 {@code amq.topic}，可在 Management UI 查看 */
    @Bean
    public TopicExchange stompTopicExchange() {
        return ExchangeBuilder.topicExchange(RabbitMqConstants.TOPIC_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue wsGreetingsQueue() {
        return QueueBuilder.durable(RabbitMqConstants.WS_GREETINGS_QUEUE).build();
    }

    @Bean
    public Queue wsNotificationsQueue() {
        return QueueBuilder.durable(RabbitMqConstants.WS_NOTIFICATIONS_QUEUE).build();
    }

    @Bean
    public Binding wsGreetingsBinding(Queue wsGreetingsQueue, TopicExchange stompTopicExchange) {
        return BindingBuilder.bind(wsGreetingsQueue)
                .to(stompTopicExchange)
                .with(RabbitMqConstants.WS_GREETINGS_ROUTING_KEY);
    }

    @Bean
    public Binding wsNotificationsBinding(Queue wsNotificationsQueue, TopicExchange stompTopicExchange) {
        return BindingBuilder.bind(wsNotificationsQueue)
                .to(stompTopicExchange)
                .with(RabbitMqConstants.WS_NOTIFICATIONS_ROUTING_KEY);
    }

    /** SSE 分布式广播：Fanout → 每实例独占 auto-delete 队列 → 本地 SseEmitter 推送 */
    @Bean
    public FanoutExchange sseFanoutExchange() {
        return ExchangeBuilder.fanoutExchange(RabbitMqConstants.SSE_FANOUT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue sseInstanceQueue() {
        return QueueBuilder.nonDurable().exclusive().autoDelete().build();
    }

    @Bean
    public Binding sseInstanceBinding(Queue sseInstanceQueue, FanoutExchange sseFanoutExchange) {
        return BindingBuilder.bind(sseInstanceQueue).to(sseFanoutExchange);
    }

    /** 发送 / 接收对象时自动转 JSON，无需手动序列化 */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter =
                new Jackson2JsonMessageConverter("com.example.demo.dto");
        return converter;
    }

    /**
     * 配合 application.yml 中的 publisher-confirm / publisher-returns，
     * 发送失败或未路由到队列时打日志，便于排查。
     * <p>
     * Spring Boot 2.7 无 {@code RabbitTemplateCustomizer}（3.1+），
     * 通过 {@link RabbitTemplateConfigurer} 保留自动配置后再追加回调。
     */
    @Bean
    public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer,
                                         ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.warn("Message not confirmed by broker, cause={}", cause);
            }
        });
        template.setReturnsCallback(returned -> log.warn(
                "Message returned: exchange={}, routingKey={}, reply={}",
                returned.getExchange(), returned.getRoutingKey(), returned.getReplyText()
        ));
        return template;
    }
}
