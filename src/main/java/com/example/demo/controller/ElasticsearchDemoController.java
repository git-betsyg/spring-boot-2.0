package com.example.demo.controller;

import com.example.demo.document.DemoArticleDocument;
import com.example.demo.entity.DemoArticle;
import com.example.demo.service.ElasticsearchDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 演示接口：MySQL 存数据，ES 做检索。
 */
@RestController
@RequiredArgsConstructor
public class ElasticsearchDemoController {

    private final ElasticsearchDemoService elasticsearchDemoService;

    @PostMapping("/es/article")
    public DemoArticle create(@RequestParam String title,
                              @RequestParam String content,
                              @RequestParam String author) {
        return elasticsearchDemoService.create(title, content, author);
    }

    @GetMapping("/es/article/{id}")
    public DemoArticle get(@PathVariable Long id) {
        return elasticsearchDemoService.getById(id);
    }

    @PutMapping("/es/article/{id}")
    public DemoArticle update(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String content,
                              @RequestParam String author) {
        return elasticsearchDemoService.update(id, title, content, author);
    }

    @DeleteMapping("/es/article/{id}")
    public void delete(@PathVariable Long id) {
        elasticsearchDemoService.delete(id);
    }

    @GetMapping("/es/article/search")
    public List<DemoArticleDocument> search(@RequestParam String keyword) {
        return elasticsearchDemoService.search(keyword);
    }

    /**
     * 从 MySQL 全量同步到 ES，分布式环境索引不一致时可手动触发。
     */
    @PostMapping("/es/article/rebuild")
    public Map<String, Integer> rebuild() {
        int count = elasticsearchDemoService.rebuildIndex();
        return Collections.singletonMap("synced", count);
    }
}
