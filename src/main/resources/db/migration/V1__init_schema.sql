-- Spring Security JDBC 认证表结构（MySQL 方言）
-- 参考：https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR(50)  NOT NULL PRIMARY KEY COMMENT '用户名',
    password VARCHAR(500) NOT NULL COMMENT '密码（DelegatingPasswordEncoder，如 {bcrypt}...）',
    enabled  TINYINT(1)  NOT NULL COMMENT '是否启用'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Spring Security 用户表';

CREATE TABLE IF NOT EXISTS authorities
(
    username  VARCHAR(50) NOT NULL COMMENT '用户名',
    authority VARCHAR(50) NOT NULL COMMENT '权限（如 ROLE_ADMIN）',
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Spring Security 用户权限表';

CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);
