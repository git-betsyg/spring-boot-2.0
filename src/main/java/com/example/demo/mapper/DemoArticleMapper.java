package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.DemoArticle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoArticleMapper extends BaseMapper<DemoArticle> {
}
