package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 手动操作 Redis 的封装（底层是 {@link RedisTemplate}）。
 * <p>
 * 和 {@code @Cacheable} 的区别：没有「缓存区 name」，key 完全由调用方自己指定。
 * 例如 {@code set("sms:code:13800138000", "123456", 5, MINUTES)}，
 * Redis 里存的就是 key={@code sms:code:13800138000}，不会自动加 {@code demo:cache:} 前缀。
 */
@Service
@RequiredArgsConstructor
public class RedisService {

    /** Spring 提供的 Redis 操作对象，序列化规则见 {@link com.example.demo.configuration.RedisConfig} */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 写入 key-value，永不过期（需手动 {@link #delete} 或 {@link #expire}）。
     *
     * @param key   完整 key，建议带业务前缀，如 {@code lock:order:1001}
     * @param value 任意对象，会以 JSON 形式存入 Redis
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 写入 key-value，并设置过期时间（最常用）。
     *
     * @param key     完整 key
     * @param value   值
     * @param timeout 过期时长
     * @param unit    时间单位，如 {@link TimeUnit#SECONDS}、{@link TimeUnit#MINUTES}
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 读取 key 对应的值。
     *
     * @return 存在则返回值，不存在返回 {@code null}
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除指定 key。
     *
     * @return {@code true} 表示 key 存在且已删除，{@code false} 表示 key 不存在
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 给已存在的 key 重新设置过期时间（不改 value）。
     *
     * @return {@code true} 表示设置成功，{@code false} 表示 key 不存在
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 判断 key 是否存在。
     *
     * @return {@code true} 表示存在，{@code false} 表示不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

}
