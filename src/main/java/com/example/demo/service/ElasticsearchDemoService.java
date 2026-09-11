package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.document.DemoArticleDocument;
import com.example.demo.dto.ArticleSyncMessage.ArticleSyncAction;
import com.example.demo.entity.DemoArticle;
import com.example.demo.exception.APIException;
import com.example.demo.mapper.DemoArticleMapper;
import com.example.demo.repository.DemoArticleDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * MySQL + Elasticsearch 演示（MQ 异步同步）：
 * <ul>
 *   <li>MySQL：增删改查、事务、数据权威来源</li>
 *   <li>RabbitMQ：事务提交后发消息，解耦写库与写 ES</li>
 *   <li>ES：全文检索；消费端从 MySQL 读最新数据再写入</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchDemoService {

    private final DemoArticleMapper articleMapper;
    private final DemoArticleDocumentRepository documentRepository;
    private final ArticleSyncProducer articleSyncProducer;
    private final ArticleElasticsearchSyncService articleElasticsearchSyncService;

    @Transactional(rollbackFor = Exception.class)
    public DemoArticle create(String title, String content, String author) {
        DemoArticle article = new DemoArticle();
        article.setTitle(title);
        article.setContent(content);
        article.setAuthor(author);
        articleMapper.insert(article);
        articleSyncProducer.sendAfterCommit(article.getId(), ArticleSyncAction.SAVE);
        return article;
    }

    public DemoArticle getById(Long id) {
        DemoArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new APIException(404, "文章不存在");
        }
        return article;
    }

    @Transactional(rollbackFor = Exception.class)
    public DemoArticle update(Long id, String title, String content, String author) {
        DemoArticle article = getById(id);
        article.setTitle(title);
        article.setContent(content);
        article.setAuthor(author);
        articleMapper.updateById(article);
        articleSyncProducer.sendAfterCommit(id, ArticleSyncAction.SAVE);
        return article;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getById(id);
        articleMapper.deleteById(id);
        articleSyncProducer.sendAfterCommit(id, ArticleSyncAction.DELETE);
    }

    /**
     * 全文检索走 ES；详情、事务写操作走 MySQL。
     */
    public List<DemoArticleDocument> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            throw new APIException(400, "keyword 不能为空");
        }
        return documentRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }

    /**
     * 从 MySQL 全量重建 ES 索引（管理接口，直接同步不走 MQ）。
     */
    public int rebuildIndex() {
        documentRepository.deleteAll();
        List<DemoArticle> articles = articleMapper.selectList(new LambdaQueryWrapper<>());
        for (DemoArticle article : articles) {
            articleElasticsearchSyncService.saveToElasticsearch(article.getId());
        }
        log.info("ES 索引重建完成, count={}", articles.size());
        return articles.size();
    }
}
