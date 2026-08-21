package com.example.demo.enums;

import com.example.demo.common.ErrorCode;

import lombok.Getter;

/**
 * API异常状态码（1000系列，范围：1000-1999）
 */
@Getter
public enum APIExceptionCode implements ErrorCode {

    REFRESH_TOKEN_MISSING(1001, "缺少 Refresh Token"),
    REFRESH_TOKEN_INVALID(1002, "Refresh Token 无效"),
    REFRESH_TOKEN_EXPIRED(1003, "Refresh Token 已过期");

    private final int errorCode;
    private final String errorMessage;

    APIExceptionCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
