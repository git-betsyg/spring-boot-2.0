package com.example.demo.configuration;

import com.example.demo.advice.ResponseVoOutboundInterceptor;
import com.example.demo.security.StompAuthErrorHandler;
import com.example.demo.security.WebSocketAuthChannelInterceptor;
import com.example.demo.security.WebSocketTokenOutboundInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP over WebSocket 配置（Spring Boot 2.7 / Spring Framework 5.3 主流写法）。
 * <p>
 * 客户端连接：{@code ws://host:port/ws}（SockJS 降级见 static/ws-demo.html）
 * <ul>
 *   <li>鉴权：STOMP CONNECT 帧携带 {@code Authorization: Bearer &lt;token&gt;}</li>
 *   <li>发送：{@code /app/...} → 服务端 {@link org.springframework.messaging.handler.annotation.MessageMapping}</li>
 *   <li>订阅：{@code /topic/...}、{@code /queue/...} → 广播 / 点对点（经 RabbitMQ STOMP 中继，支持多实例）</li>
 * </ul>
 * RabbitMQ 需启用 STOMP 插件：{@code rabbitmq-plugins enable rabbitmq_stomp}（默认端口 61613）。
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /** RabbitMQ STOMP 插件默认端口（与 AMQP 5672 不同） */
    private static final int STOMP_RELAY_PORT = 61613;

    private final RabbitProperties rabbitProperties;
    private final WebSocketAuthChannelInterceptor authChannelInterceptor;
    private final WebSocketTokenOutboundInterceptor tokenOutboundInterceptor;
    private final ResponseVoOutboundInterceptor responseVoOutboundInterceptor;
    private final StompAuthErrorHandler stompAuthErrorHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setErrorHandler(stompAuthErrorHandler);
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 把 /topic、/queue 交给 RabbitMQ（STOMP 中继），多实例部署时广播仍有效
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(rabbitProperties.getHost())           // STOMP 中继地址（RabbitMQ 主机）
                .setRelayPort(STOMP_RELAY_PORT)                       // STOMP 插件端口，默认 61613
                .setClientLogin(rabbitProperties.getUsername())       // 应用 → RabbitMQ 出站连接的登录名
                .setClientPasscode(rabbitProperties.getPassword())    // 出站连接密码
                .setSystemLogin(rabbitProperties.getUsername())       // 系统连接登录名（订阅 / 监听 topic、queue）
                .setSystemPasscode(rabbitProperties.getPassword())    // 系统连接密码
                .setVirtualHost(rabbitProperties.getVirtualHost())    // RabbitMQ 虚拟主机
                .setSystemHeartbeatSendInterval(5000)                 // 系统连接发送心跳间隔（ms），保活
                .setSystemHeartbeatReceiveInterval(4000);             // 系统连接期望接收心跳间隔（ms）
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // 先检查 token（过期则 STOMP ERROR），再包装 ResponseVo
        registration.interceptors(tokenOutboundInterceptor, responseVoOutboundInterceptor);
    }
}
