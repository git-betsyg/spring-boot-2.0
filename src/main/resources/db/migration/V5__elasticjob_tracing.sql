-- ElasticJob 事件追踪表（字段与官方 3.0.x MySQL.properties 一致；时间列用 DATETIME 避免 TIMESTAMP 时区转换）
-- 作业执行记录：每次分片执行的开始/结束/成败
CREATE TABLE IF NOT EXISTS JOB_EXECUTION_LOG
(
    auto_id         INT          NOT NULL AUTO_INCREMENT,
    id              VARCHAR(40)  NOT NULL,
    job_name        VARCHAR(100) NOT NULL,
    task_id         VARCHAR(255) NOT NULL,
    hostname        VARCHAR(255) NOT NULL,
    ip              VARCHAR(50)  NOT NULL,
    sharding_item   INT          NOT NULL,
    execution_source VARCHAR(20) NOT NULL,
    failure_cause   VARCHAR(4000) NULL,
    is_success      INT          NOT NULL,
    start_time      DATETIME     NULL,
    complete_time   DATETIME     NULL,
    PRIMARY KEY (auto_id),
    UNIQUE KEY uk_id (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'ElasticJob 作业执行日志';

-- 作业状态轨迹：任务级状态流转（分片、故障转移等）
CREATE TABLE IF NOT EXISTS JOB_STATUS_TRACE_LOG
(
    auto_id          INT          NOT NULL AUTO_INCREMENT,
    id               VARCHAR(40)  NOT NULL,
    job_name         VARCHAR(100) NOT NULL,
    original_task_id VARCHAR(255) NOT NULL,
    task_id          VARCHAR(255) NOT NULL,
    slave_id         VARCHAR(50)  NOT NULL,
    source           VARCHAR(50)  NOT NULL,
    execution_type   VARCHAR(20)  NOT NULL,
    sharding_item    VARCHAR(100) NOT NULL,
    state            VARCHAR(20)  NOT NULL,
    message          VARCHAR(4000) NULL,
    creation_time    DATETIME     NULL,
    PRIMARY KEY (auto_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'ElasticJob 作业状态轨迹';

CREATE INDEX TASK_ID_STATE_INDEX ON JOB_STATUS_TRACE_LOG (task_id(128), state);
