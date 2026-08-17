-- 初始表结构（Flyway 命名：V<版本>__<描述>.sql）
CREATE TABLE IF NOT EXISTS t_user
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username    VARCHAR(64)  NOT NULL COMMENT '用户名',
    email       VARCHAR(128)          COMMENT '邮箱',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='用户表';
