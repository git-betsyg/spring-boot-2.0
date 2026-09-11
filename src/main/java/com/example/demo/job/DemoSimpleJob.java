package com.example.demo.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

/**
 * ElasticJob 简单任务示例：实现 {@link SimpleJob}，由 Starter 按 cron 调度。
 * <p>
 * 配置见 {@code application-{profile}.yml} 中 {@code elasticjob.jobs.demoSimpleJob}。
 */
@Slf4j
@Component
public class DemoSimpleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {
        log.info("ElasticJob 执行: job={}, 分片 {}/{}, 参数={}",
                context.getJobName(),
                context.getShardingItem(),
                context.getShardingTotalCount(),
                context.getShardingParameter());
    }
}
