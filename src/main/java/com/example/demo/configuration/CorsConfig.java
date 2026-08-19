package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * 全局跨域（CORS）配置，供 Spring Security 的 {@code .cors()} 使用。
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许任意来源；使用 Pattern 而非 Origin，以便与 allowCredentials 同时生效
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 允许的 HTTP 方法（含 OPTIONS 预检）
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 允许前端携带的任意请求头（如 Authorization、Content-Type）
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        // 允许前端 JS 读取的响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        // 允许携带 Cookie、Authorization 等凭证
        configuration.setAllowCredentials(true);
        // 预检请求缓存时间（秒），减少重复 OPTIONS 请求
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
