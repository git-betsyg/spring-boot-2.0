package com.example.demo.service;

import com.example.demo.batch.BatchJobResult;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 手动触发 Spring Batch Job 的 Service。
 * <p>
 * 整体流程：接口 POST /batch/demo/run → 本类启动 Job → Job 在后台按 Step 读库、处理、写库
 * （Job 的具体逻辑在 BatchDemoConfig：把 status=PENDING 的记录改成 PROCESSED）。
 */
@Service
public class BatchDemoService {

    /** Spring Batch 提供的“启动器”，负责把 Job 跑起来 */
    private final JobLauncher jobLauncher;

    /** 要执行的那条批处理任务，对应 BatchDemoConfig 里 @Bean 注册的 processDemoRecordsJob */
    private final Job processDemoRecordsJob;

    public BatchDemoService(JobLauncher jobLauncher,
                            @Qualifier("processDemoRecordsJob") Job processDemoRecordsJob) {
        this.jobLauncher = jobLauncher;
        this.processDemoRecordsJob = processDemoRecordsJob;
    }

    /**
     * 立即执行一次批处理，并等待跑完后把结果返回给接口。
     * <p>
     * 步骤简述：
     * 1. 构造 JobParameters（本次运行的“入参”，Spring Batch 会用它区分每一次执行）
     * 2. jobLauncher.run(...) 同步启动 Job，方法返回时 Job 已结束
     * 3. 从 JobExecution 里取出 Job 名、最终状态、各 Step 累计读/写条数，封装成 BatchJobResult
     */
    public BatchJobResult runProcessDemoRecordsJob() throws Exception {
        // Spring Batch 规定：完全相同的 JobParameters 不能重复跑同一个 Job。
        // 所以每次带上当前时间戳，保证“这次运行”和“上次运行”参数不同，可以反复手动触发。
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // 键名 time，值为当前毫秒时间戳，仅用于区分每次运行
                .toJobParameters();                          // 构建成 JobParameters，供 jobLauncher.run 使用

        // 启动 Job；此处会阻塞直到 Job 执行完毕（成功或失败）
        JobExecution execution = jobLauncher.run(processDemoRecordsJob, params);

        // 汇总各 Step 的统计信息，供接口直接返回给前端/调用方
        return new BatchJobResult(
                execution.getJobInstance().getJobName(),   // 例如 processDemoRecordsJob
                execution.getStatus().name(),                // 例如 COMPLETED、FAILED
                execution.getStepExecutions().stream().mapToLong(step -> step.getReadCount()).sum(),
                execution.getStepExecutions().stream().mapToLong(step -> step.getWriteCount()).sum()
        );
    }
}
