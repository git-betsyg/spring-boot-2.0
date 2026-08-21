-- Refresh Token 持久化表（配合 HttpOnly Cookie 使用）
CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username   VARCHAR(50)  NOT NULL COMMENT '用户名',
    token      VARCHAR(64)  NOT NULL COMMENT 'Refresh Token 值（UUID）',
    expires_at DATETIME     NOT NULL COMMENT '过期时间（UTC）',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（UTC）',
    CONSTRAINT uk_refresh_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_users FOREIGN KEY (username) REFERENCES users (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Refresh Token 表';

CREATE INDEX idx_refresh_tokens_username ON refresh_tokens (username);
