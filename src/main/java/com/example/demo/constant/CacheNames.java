package com.example.demo.constant;

/**
 * Spring Cache 缓存区名称，与 application.yml 中 spring.cache.cache-names 保持一致。
 */
public final class CacheNames {

    /** 演示缓存区，默认 TTL 10 分钟 */
    public static final String DEMO = "demo";

    /** 用户信息缓存区，TTL 5 分钟（见 RedisConfig） */
    public static final String USERS = "users";

    private CacheNames() {
    }
}
