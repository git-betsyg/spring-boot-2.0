package com.example.demo.controller;

import com.example.demo.service.CacheDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存演示接口：连续两次 GET 同一 id，第二次不会打印「缓存未命中」日志。
 */
@RestController
@RequiredArgsConstructor
public class CacheDemoController {

    private final CacheDemoService cacheDemoService;

    @GetMapping("/cache/demo/{id}")
    public String get(@PathVariable String id) {
        return cacheDemoService.getById(id);
    }

    @PutMapping("/cache/demo/{id}")
    public String update(@PathVariable String id, @RequestParam String value) {
        return cacheDemoService.update(id, value);
    }

    @DeleteMapping("/cache/demo/{id}")
    public void evict(@PathVariable String id) {
        cacheDemoService.evict(id);
    }
}
