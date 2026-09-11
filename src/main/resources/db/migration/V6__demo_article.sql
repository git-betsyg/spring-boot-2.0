-- 文章表：MySQL 作为数据源（CRUD、事务），Elasticsearch 作为检索索引
CREATE TABLE IF NOT EXISTS demo_article
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title      VARCHAR(200) NOT NULL COMMENT '标题',
    content    TEXT         NOT NULL COMMENT '正文',
    author     VARCHAR(50)  NOT NULL COMMENT '作者',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '演示文章（MySQL 主库 + ES 检索）';

CREATE INDEX idx_demo_article_author ON demo_article (author);
