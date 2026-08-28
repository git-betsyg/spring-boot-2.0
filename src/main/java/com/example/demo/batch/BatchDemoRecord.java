package com.example.demo.batch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批处理演示：一条待处理记录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDemoRecord {

    private Long id;
    private String content;
    private String status;
}
