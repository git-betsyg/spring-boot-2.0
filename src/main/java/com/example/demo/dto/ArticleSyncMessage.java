package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章同步 ES 的 MQ 消息体（只传 id + 操作类型，消费端从 MySQL 读最新数据）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSyncMessage {

    private Long id;

    private ArticleSyncAction action;

    public enum ArticleSyncAction {
        /** 新增或更新后写入 ES */
        SAVE,
        /** 删除后从 ES 移除 */
        DELETE
    }
}
