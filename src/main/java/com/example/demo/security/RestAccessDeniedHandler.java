package com.example.demo.security;

import com.example.demo.common.ResponseVo;
import com.example.demo.enums.SecurityExceptionCode;
import com.example.demo.service.I18nMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final I18nMessageService i18nMessageService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        SecurityExceptionCode code = SecurityExceptionCode.FORBIDDEN;
        String message = i18nMessageService.getMessage(code.getErrorMessage());
        ResponseVo vo = new ResponseVo(code.getErrorCode(), message);
        response.getWriter().write(objectMapper.writeValueAsString(vo));
    }
}
