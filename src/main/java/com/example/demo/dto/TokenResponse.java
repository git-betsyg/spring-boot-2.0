package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Access Token 响应（Refresh Token 通过 HttpOnly Cookie 下发，不在 body 中返回）
 */
@Data
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;

}
