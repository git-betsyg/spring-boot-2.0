package com.example.demo.advice;

import com.example.demo.common.ResponseVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一包装响应
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.example.demo")
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // response是ResponseVo类型的不进行包装
        return !ResponseVo.class.isAssignableFrom(returnType.getParameterType())
                // 有NotControllerResponseAdvice注解的不进行包装
                && !returnType.hasMethodAnnotation(NotControllerResponseAdvice.class);
    }

    @Override
    @SneakyThrows
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseVo) {
            return body;
        }
        ResponseVo responseVo = new ResponseVo(body);
        // String 返回类型会走 StringHttpMessageConverter，必须先序列化成 JSON 字符串
        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)
                || String.class.isAssignableFrom(returnType.getParameterType())) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(responseVo);
        }
        return responseVo;
    }
}
