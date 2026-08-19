package com.example.demo.common;

import com.example.demo.enums.ShowType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 统一返回接口类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ResponseVo {

    /**
     * 请求是否成功
     */
    private boolean success;

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 错误类型代码
     */
    private Integer errorCode;

    /**
     * 显示给用户的错误信息
     */
    private String errorMessage;

    /**
     * 错误显示类型
     */
    private Integer showType;

    /**
     * 方便后端故障排除：唯一请求ID
     */
    private String traceId;

    /**
     * 方便后端故障排除：当前访问服务器的主机名
     */
    private String host;

    /**
     * 返回成功状态
     *
     * @param data 响应数据
     */
    public ResponseVo(Object data) {
        this.success = true;
        this.data = data;
    }

    /**
     * 返回错误状态
     *
     * @param errorCode    错误类型代码
     * @param errorMessage 显示给用户的错误信息
     */
    public ResponseVo(int errorCode, String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    /**
     * 返回错误状态
     *
     * @param errorCode    错误类型代码
     * @param errorMessage 显示给用户的错误信息
     * @param showType     错误显示类型
     */
    public ResponseVo(int errorCode, String errorMessage, ShowType showType) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.showType = showType.getValue();
    }

    /**
     * 返回错误状态
     *
     * @param errorCode    错误类型代码
     * @param errorMessage 显示给用户的错误信息
     * @param data         响应数据
     */
    public ResponseVo(int errorCode, String errorMessage, Object data) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.data = data;
    }

    /**
     * 返回错误状态
     *
     * @param errorCode    错误类型代码
     * @param errorMessage 显示给用户的错误信息
     * @param data         响应数据
     * @param showType     错误显示类型
     */
    public ResponseVo(int errorCode, String errorMessage, Object data, ShowType showType) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.data = data;
        this.showType = showType.getValue();
    }
}
