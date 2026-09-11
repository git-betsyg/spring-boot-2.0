package com.example.demo.service;

import com.example.demo.document.DemoArticleDocument;
import com.example.demo.entity.DemoArticle;
import com.example.demo.mapper.DemoArticleMapper;
import com.example.demo.repository.DemoArticleDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实际执行 MySQL → ES 同步的逻辑，由 MQ 消费者调用。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleElasticsearchSyncService {

    private final DemoArticleMapper articleMapper;
    private final DemoArticleDocumentRepository documentRepository;

    public void saveToElasticsearch(Long id) {
        DemoArticle article = articleMapper.selectById(id);
        if (article == null) {
            log.warn("文章不存在，跳过 ES 写入, id={}", id);
            return;
        }
        documentRepository.save(toDocument(article));
        log.info("ES 同步完成, id={}", id);
    }

    public void deleteFromElasticsearch(Long id) {
        documentRepository.deleteById(id);
        log.info("ES 删除完成, id={}", id);
    }

    private DemoArticleDocument toDocument(DemoArticle article) {
        DemoArticleDocument document = new DemoArticleDocument();
        document.setId(article.getId());
        document.setTitle(article.getTitle());
        document.setContent(article.getContent());
        document.setAuthor(article.getAuthor());
        document.setCreatedAt(article.getCreatedAt());
        return document;
    }
}
