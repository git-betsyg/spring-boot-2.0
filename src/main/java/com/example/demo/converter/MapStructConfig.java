package com.example.demo.converter;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct 全局配置，业务 Converter 通过 {@code @Mapper(config = MapStructConfig.class)} 继承。
 */
@MapperConfig(
        componentModel = "spring", // 所有 Mapper 自动注册为 Spring Bean
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // 目标对象有未映射的字段时，不报错也不警告
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE // 忽略 null 值的映射
)
public interface MapStructConfig {
}
