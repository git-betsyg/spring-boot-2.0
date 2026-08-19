package com.example.demo.enums;

import com.example.demo.common.ErrorCode;
import lombok.Getter;

/**
 * 全局异常状态码（1000以下，客户端异常，范围：400-499，服务端异常，范围：500-599）
 */
@Getter
public enum ExceptionCode implements ErrorCode {

    EXCEPTION_ERROR(500, "服务器错误"),
    // @Validated 校验异常
    BINDEXCEPTION_ERROR(400, "请求错误");

    // 错误类型代码
    private final int errorCode;
    // 显示给用户的错误信息
    private final String errorMessage;

    ExceptionCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
