-- 批处理演示业务表：读取 PENDING 记录，处理后标记为 PROCESSED
CREATE TABLE IF NOT EXISTS batch_demo_record
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    content VARCHAR(255) NOT NULL COMMENT '待处理内容',
    status  VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / PROCESSED'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Spring Batch 演示表';

INSERT INTO batch_demo_record (content, status)
VALUES ('hello batch', 'PENDING'),
       ('spring boot 2.7', 'PENDING'),
       ('demo item 3', 'PENDING');
