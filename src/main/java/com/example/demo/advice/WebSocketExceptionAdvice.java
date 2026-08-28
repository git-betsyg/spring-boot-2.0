package com.example.demo.advice;

import cn.hutool.core.util.ObjectUtil;
import com.example.demo.common.ResponseVo;
import com.example.demo.enums.ExceptionCode;
import com.example.demo.enums.SecurityExceptionCode;
import com.example.demo.exception.APIException;
import com.example.demo.service.I18nMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * WebSocket {@link org.springframework.messaging.handler.annotation.MessageMapping} 全局异常处理。
 * <p>
 * 捕获 {@code @MessageMapping} 抛出的异常，统一包装为 {@link ResponseVo}（{@code success=false}），
 * 并通过 {@code /user/queue/errors} 推送给消息发送者。
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionAdvice {

    private final I18nMessageService i18nMessageService;

    @MessageExceptionHandler(APIException.class)
    @SendToUser("/queue/errors")
    public ResponseVo apiExceptionHandler(APIException e) {
        log.error(e.getMessage(), e);

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

    @MessageExceptionHandler(BindException.class)
    @SendToUser("/queue/errors")
    public ResponseVo bindExceptionHandler(BindException e) {
        log.error(e.getMessage(), e);
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return new ResponseVo(ExceptionCode.BINDEXCEPTION_ERROR.getErrorCode(), objectError.getDefaultMessage());
    }

    @MessageExceptionHandler(AccessDeniedException.class)
    @SendToUser("/queue/errors")
    public ResponseVo accessDeniedExceptionHandler(AccessDeniedException e) {
        log.error(e.getMessage(), e);
        String message = i18nMessageService.getMessage(SecurityExceptionCode.FORBIDDEN.getErrorMessage());
        return new ResponseVo(SecurityExceptionCode.FORBIDDEN.getErrorCode(), message);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ResponseVo exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        String message = i18nMessageService.getMessage(ExceptionCode.EXCEPTION_ERROR.getErrorMessage());
        return new ResponseVo(ExceptionCode.EXCEPTION_ERROR.getErrorCode(), message);
    }
}
