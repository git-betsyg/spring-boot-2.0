package com.example.demo.exception;

import com.example.demo.enums.ShowType;
import lombok.Getter;

/**
 * API异常
 */
@Getter
public class APIException extends RuntimeException {

    // 错误类型代码
    private final int errorCode;
    // 显示给用户的错误信息
    private final String errorMessage;
    // 错误显示类型
    private ShowType showType;
    // 响应数据
    private Object data;

    /**
     * API异常
     * @param errorCode    错误类型代码
     * @param errorMessage 显示给用户的错误信息
     */
    public APIException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * API异常（可设置错误显示类型）
     * @param errorCode    错误类型代码
     * @param errorMessage 显示给用户的错误信息
     * @param showType     错误显示类型
     */
    public APIException(int errorCode, String errorMessage, ShowType showType) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.showType = showType;
    }

    /**
     * API异常（可设置响应数据）
     * @param errorCode    错误类型代码
     * @param errorMessage 显示给用户的错误信息
     * @param data         响应数据
     */
    public APIException(int errorCode, String errorMessage, Object data) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    /**
     * API异常（可设置响应数据及错误显示类型）
     * @param errorCode    错误类型代码
     * @param errorMessage 显示给用户的错误信息
     * @param data         响应数据
     * @param showType     错误显示类型
     */
    public APIException(int errorCode, String errorMessage, Object data, ShowType showType) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
        this.showType = showType;
    }
}
