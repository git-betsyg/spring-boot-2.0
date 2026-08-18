package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        return "Hello, " + authentication.getName() + "!";
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "world") String name) {
        log.trace("trace 级别日志，需开启 TRACE 才会输出");
        log.debug("收到请求, name={}", name);
        log.info("处理 /hello 请求, name={}", name);

        if ("warn".equalsIgnoreCase(name)) {
            log.warn("name 参数为 warn，触发警告日志示例");
        }
        if ("error".equalsIgnoreCase(name)) {
            log.error("name 参数为 error，触发错误日志示例");
        }

        return "Hello, " + name;
    }

}
