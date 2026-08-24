package com.example.demo.configuration;

import com.example.demo.constant.RabbitMqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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
