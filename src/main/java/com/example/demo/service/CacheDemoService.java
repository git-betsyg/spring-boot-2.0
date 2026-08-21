package com.example.demo.service;

import com.example.demo.constant.CacheNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Spring Cache 用法示例，可在任意 Service 中按同样方式使用注解。
 */
@Slf4j
@Service
@CacheConfig(cacheNames = CacheNames.DEMO)
public class CacheDemoService {

    /**
     * 查询：命中缓存则不再执行方法体。
     * key 默认是方法参数；此处显式指定 SpEL 便于理解。
     */
    @Cacheable(key = "#id")
    public String getById(String id) {
        log.info("缓存未命中，模拟耗时查询, id={}", id);
        return "data-" + id;
    }

    /**
     * 更新：执行方法并将返回值写入缓存（refresh）。
     */
    @CachePut(key = "#id")
    public String update(String id, String value) {
        log.info("更新数据并刷新缓存, id={}, value={}", id, value);
        return value;
    }

    /**
     * 删除：移除指定 key 的缓存条目。
     */
    @CacheEvict(key = "#id")
    public void evict(String id) {
        log.info("清除缓存, id={}", id);
    }
}
