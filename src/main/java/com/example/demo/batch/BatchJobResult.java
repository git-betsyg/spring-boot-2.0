package com.example.demo.batch;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 手动触发批处理 Job 后的简要结果。
 */
@Data
@AllArgsConstructor
public class BatchJobResult {

    private String jobName;
    private String status;
    private long readCount;
    private long writeCount;
}
