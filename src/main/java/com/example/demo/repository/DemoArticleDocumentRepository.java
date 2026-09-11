package com.example.demo.repository;

import com.example.demo.document.DemoArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Spring Data Elasticsearch 仓库，提供全文检索能力。
 */
public interface DemoArticleDocumentRepository extends ElasticsearchRepository<DemoArticleDocument, Long> {

    /**
     * 标题或正文包含关键词（适合演示；生产可改用 @Query 做 multi_match 评分排序）。
     */
    List<DemoArticleDocument> findByTitleContainingOrContentContaining(String title, String content);
}
