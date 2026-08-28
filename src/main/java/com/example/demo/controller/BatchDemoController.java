package com.example.demo.controller;

import com.example.demo.batch.BatchJobResult;
import com.example.demo.service.BatchDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Batch 演示：手动触发批处理 Job。
 * <p>
 * 需先执行 Flyway V4 脚本创建 batch_demo_record 表及示例数据。
 */
@RestController
@RequiredArgsConstructor
public class BatchDemoController {

    private final BatchDemoService batchDemoService;

    @PostMapping("/batch/demo/run")
    public BatchJobResult run() throws Exception {
        return batchDemoService.runProcessDemoRecordsJob();
    }
}
