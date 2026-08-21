package com.example.demo.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearer-jwt";
    public static final String BASIC_SCHEME = "basicAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo API")
                        .description("登录：POST /token（Basic Auth）获取短期 Access Token，Refresh Token 写入 HttpOnly Cookie。"
                                + "刷新：POST /token/refresh（自动携带 Cookie）。"
                                + "业务 API 也可直接使用 Basic Auth 或 Bearer JWT。"
                                + "前端跨域调用时须开启 credentials（如 fetch 的 credentials: 'include'、axios 的 withCredentials: true），"
                                + "否则浏览器不会发送 Cookie，刷新 Token 将失败；后端已配置 Access-Control-Allow-Credentials。"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token，从 POST /token 接口获取"))
                        .addSecuritySchemes(BASIC_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Basic Auth，用户名 user / 密码 password（也可直接访问 API，无需先取 token）")))
                // 二选一：Bearer JWT 或 Basic Auth
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_SCHEME));
    }

}
