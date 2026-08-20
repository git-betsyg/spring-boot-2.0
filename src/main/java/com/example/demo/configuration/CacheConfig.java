package com.example.demo.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 缓存补充配置。
 * <p>
 * 基础参数优先使用 application.yml 中的 {@code spring.cache.caffeine.spec}；
 * 此处演示如何为单个 cache 区做差异化配置（官方文档 CacheManagerCustomizer 方式）。
 */
@Configuration(proxyBeanMethods = false)
public class CacheConfig {

    /**
     * 为 users 缓存区单独设置更短的 TTL（5 分钟），其余 cache 仍走 yml 中的 spec。
     */
    @Bean
    public CacheManagerCustomizer<CaffeineCacheManager> caffeineCacheManagerCustomizer() {
        return cacheManager -> cacheManager.registerCustomCache("users",
                Caffeine.newBuilder()
                        .maximumSize(200)
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .build());
    }
}
