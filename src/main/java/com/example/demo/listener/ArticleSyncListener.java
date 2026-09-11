package com.example.demo.listener;

import com.example.demo.constant.RabbitMqConstants;
import com.example.demo.dto.ArticleSyncMessage;
import com.example.demo.dto.ArticleSyncMessage.ArticleSyncAction;
import com.example.demo.service.ArticleElasticsearchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费文章同步消息，异步写入 / 删除 ES。
 * 失败时由 application.yml 中 spring.rabbitmq.listener.simple.retry 自动重试。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleSyncListener {

    private final ArticleElasticsearchSyncService syncService;

    @RabbitListener(queues = RabbitMqConstants.ES_ARTICLE_SYNC_QUEUE)
    public void onMessage(ArticleSyncMessage message) {
        log.info("收到文章同步消息: id={}, action={}", message.getId(), message.getAction());
        if (message.getAction() == ArticleSyncAction.SAVE) {
            syncService.saveToElasticsearch(message.getId());
        } else {
            syncService.deleteFromElasticsearch(message.getId());
        }
    }
}
