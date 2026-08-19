package com.example.demo.advice;

import cn.hutool.core.util.ObjectUtil;
import com.example.demo.common.ResponseVo;
import com.example.demo.enums.ExceptionCode;
import com.example.demo.exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice(basePackages = "com.example.demo")
@Slf4j
public class ControllerExceptionAdvice {

    // 捕获 Exception
    @ExceptionHandler(Exception.class)
    public ResponseVo exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseVo(ExceptionCode.EXCEPTION_ERROR.getErrorCode(), e.getMessage());
    }

    // 捕获 @Validated 校验异常
    @ExceptionHandler(BindException.class)
    public ResponseVo bindExceptionExceptionHandler(BindException e) {
        log.error(e.getMessage(), e);
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return new ResponseVo(ExceptionCode.BINDEXCEPTION_ERROR.getErrorCode(), objectError.getDefaultMessage());
    }

    // 捕获API异常
    @ExceptionHandler(APIException.class)
    public ResponseVo apiExceptionHandler(APIException e) {
        log.error(e.getMessage(), e);

        // 判断是否存在数据和错误显示类型
        boolean hasData = ObjectUtil.isNotNull(e.getData());
        boolean hasShowType = ObjectUtil.isNotNull(e.getShowType());

        if (hasData && hasShowType) {
            return new ResponseVo(e.getErrorCode(), e.getErrorMessage(), e.getData(), e.getShowType());
        } else if (hasData) {
            return new ResponseVo(e.getErrorCode(), e.getErrorMessage(), e.getData());
        } else if (hasShowType) {
            return new ResponseVo(e.getErrorCode(), e.getErrorMessage(), e.getShowType());
        } else {
            return new ResponseVo(e.getErrorCode(), e.getErrorMessage());
        }
    }
}
