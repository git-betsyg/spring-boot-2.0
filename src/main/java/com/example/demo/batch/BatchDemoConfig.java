package com.example.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Spring Batch 批处理 Job 的 Java 配置类（不是 application.yml 那种配置文件）。
 * <p>
 * 定义「读 → 处理 → 写」三步流水线：从 {@code batch_demo_record} 读取 PENDING 记录，
 * 追加 {@code [processed]} 并标记为 PROCESSED，再写回数据库。
 * <p>
 * 运行时开关、表初始化等见 {@code application.yml} 中 {@code spring.batch.*}。
 */
@Configuration
@EnableBatchProcessing  // 启用 Spring Batch，注册 JobLauncher、JobRepository 等基础设施
public class BatchDemoConfig {

    /** 每批提交的事务大小：每处理 10 条记录提交一次 */
    private static final int CHUNK_SIZE = 10;

    private final DataSource dataSource;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public BatchDemoConfig(DataSource dataSource,
                           JobBuilderFactory jobBuilderFactory,
                           StepBuilderFactory stepBuilderFactory) {
        this.dataSource = dataSource;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    /** 批处理 Job 入口：每次运行自动递增 run id，便于区分多次执行 */
    @Bean
    public Job processDemoRecordsJob(Step processDemoRecordsStep) {
        return jobBuilderFactory.get("processDemoRecordsJob")
                .incrementer(new RunIdIncrementer())
                .start(processDemoRecordsStep)
                .build();
    }

    /** 单个 Step：chunk 模式 = reader 读一批 → processor 转换 → writer 批量写入 */
    @Bean
    public Step processDemoRecordsStep() {
        return stepBuilderFactory.get("processDemoRecordsStep")
                .<BatchDemoRecord, BatchDemoRecord>chunk(CHUNK_SIZE)
                .reader(demoRecordReader())
                .processor(demoRecordProcessor())
                .writer(demoRecordWriter())
                .build();
    }

    /** Reader：游标方式逐条读取 status = PENDING 的记录 */
    @Bean
    @StepScope  // 每次 Step 执行时创建新实例，避免游标状态在多 Job 间复用
    public JdbcCursorItemReader<BatchDemoRecord> demoRecordReader() {
        return new JdbcCursorItemReaderBuilder<BatchDemoRecord>()
                .name("demoRecordReader")
                .dataSource(dataSource)
                .sql("SELECT id, content, status FROM batch_demo_record WHERE status = 'PENDING' ORDER BY id")
                .rowMapper((rs, rowNum) -> new BatchDemoRecord(
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getString("status")
                ))
                .build();
    }

    /** Processor：业务转换逻辑，此处为演示用的简单字符串拼接与状态变更 */
    @Bean
    public ItemProcessor<BatchDemoRecord, BatchDemoRecord> demoRecordProcessor() {
        return record -> {
            record.setContent(record.getContent() + " [processed]");
            record.setStatus("PROCESSED");
            return record;
        };
    }

    /** Writer：按 id 批量 UPDATE，字段名与 {@link BatchDemoRecord} 属性自动映射 */
    @Bean
    public JdbcBatchItemWriter<BatchDemoRecord> demoRecordWriter() {
        return new JdbcBatchItemWriterBuilder<BatchDemoRecord>()
                .dataSource(dataSource)
                .sql("UPDATE batch_demo_record SET content = :content, status = :status WHERE id = :id")
                .beanMapped()
                .build();
    }
}
