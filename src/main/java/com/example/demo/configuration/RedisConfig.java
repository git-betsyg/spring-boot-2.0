package com.example.demo.configuration;

import com.example.demo.constant.CacheNames;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/** Redis 配置：{@code @Cacheable} 注解缓存 + {@link RedisTemplate} 手动操作 */
@Configuration(proxyBeanMethods = false)
public class RedisConfig {

    /** key 字符串序列化，value JSON 序列化 */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /** {@code @Cacheable} 默认缓存规则，TTL 10 分钟 */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return baseCacheConfig(Duration.ofMinutes(10));
    }

    /** 按 cache name 覆盖 TTL，如 users 设为 5 分钟 */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            RedisCacheConfiguration redisCacheConfiguration) {
        return builder -> builder
                .cacheDefaults(redisCacheConfiguration)
                .withCacheConfiguration(CacheNames.USERS, redisCacheConfiguration.entryTtl(Duration.ofMinutes(5)));
    }

    private static RedisCacheConfiguration baseCacheConfig(Duration ttl) {
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                .disableCachingNullValues();
    }
}
