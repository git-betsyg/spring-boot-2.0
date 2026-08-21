package com.example.demo.controller;

import com.example.demo.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * RedisTemplate 直连接口演示（非 @Cacheable 场景）。
 */
@RestController
@RequiredArgsConstructor
public class RedisDemoController {

    private final RedisService redisService;

    @GetMapping("/cache/redis/{key}")
    public Object get(@PathVariable String key) {
        return redisService.get(key);
    }

    @PutMapping("/cache/redis/{key}")
    public void set(@PathVariable String key,
                    @RequestParam String value,
                    @RequestParam(defaultValue = "300") long ttlSeconds) {
        redisService.set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    @DeleteMapping("/cache/redis/{key}")
    public boolean delete(@PathVariable String key) {
        return Boolean.TRUE.equals(redisService.delete(key));
    }
}
