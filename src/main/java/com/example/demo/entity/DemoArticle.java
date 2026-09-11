package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章实体，对应 MySQL demo_article 表（数据源）。
 */
@Data
@TableName("demo_article")
public class DemoArticle {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String author;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
