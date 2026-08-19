package com.example.demo.advice;

import com.example.demo.common.ResponseVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
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
        return !(returnType.getParameterType().isAssignableFrom(ResponseVo.class)
                // 有NotControllerResponseAdvice注解的不进行包装
                || returnType.hasMethodAnnotation(NotControllerResponseAdvice.class));
    }

    @Override
    @SneakyThrows
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // String类型不能直接包装，需要进行处理
        if (returnType.getGenericParameterType().equals(String.class)) {
            ObjectMapper objectMapper = new ObjectMapper();
            // 将数据包装在ResponseVo中转换为json进行返回
            return objectMapper.writeValueAsString(new ResponseVo(body));
        }
        return new ResponseVo(body);
    }
}
