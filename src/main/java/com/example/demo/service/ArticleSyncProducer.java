package com.example.demo.service;

import com.example.demo.constant.RabbitMqConstants;
import com.example.demo.dto.ArticleSyncMessage;
import com.example.demo.dto.ArticleSyncMessage.ArticleSyncAction;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 事务提交后再发 MQ，避免 MySQL 回滚但消息已发出的不一致。
 */
@Service
@RequiredArgsConstructor
public class ArticleSyncProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendAfterCommit(Long id, ArticleSyncAction action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(id, action);
                }
            });
        } else {
            send(id, action);
        }
    }

    private void send(Long id, ArticleSyncAction action) {
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.EXCHANGE,
                RabbitMqConstants.ES_ARTICLE_SYNC_ROUTING_KEY,
                new ArticleSyncMessage(id, action)
        );
    }
}
