package com.example.demo.enums;

import com.example.demo.common.ErrorCode;
import lombok.Getter;

@Getter
public enum SecurityExceptionCode implements ErrorCode {

    UNAUTHORIZED(401, "error.security.unauthorized"),
    FORBIDDEN(403, "error.security.forbidden");

    private final int errorCode;
    private final String errorMessage;

    SecurityExceptionCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
