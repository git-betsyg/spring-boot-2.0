package com.example.demo.advice;

import cn.hutool.core.util.ObjectUtil;
import com.example.demo.common.ResponseVo;
import com.example.demo.enums.ExceptionCode;
import com.example.demo.enums.SecurityExceptionCode;
import com.example.demo.exception.APIException;
import com.example.demo.service.I18nMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice(basePackages = "com.example.demo")
@Slf4j
@RequiredArgsConstructor
public class ControllerExceptionAdvice {

    private final I18nMessageService i18nMessageService;

    // 捕获 Exception
    @ExceptionHandler(Exception.class)
    public ResponseVo exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        String message = i18nMessageService.getMessage(ExceptionCode.EXCEPTION_ERROR.getErrorMessage());
        return new ResponseVo(ExceptionCode.EXCEPTION_ERROR.getErrorCode(), message);
    }

    // 捕获 @Validated 校验异常
    @ExceptionHandler(BindException.class)
    public ResponseVo bindExceptionExceptionHandler(BindException e) {
        log.error(e.getMessage(), e);
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return new ResponseVo(ExceptionCode.BINDEXCEPTION_ERROR.getErrorCode(), objectError.getDefaultMessage());
    }

    // 捕获权限不足异常（如 @PreAuthorize）
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseVo accessDeniedExceptionHandler(AccessDeniedException e) {
        log.error(e.getMessage(), e);
        String message = i18nMessageService.getMessage(SecurityExceptionCode.FORBIDDEN.getErrorMessage());
        return new ResponseVo(SecurityExceptionCode.FORBIDDEN.getErrorCode(), message);
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
