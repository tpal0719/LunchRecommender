package com.sparta.lunchrecommender.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class Logging {

    private final HttpServletRequest request;
    public Logging(HttpServletRequest request) {
        this.request = request;
    }

    @Before("execution(* com.sparta.lunchrecommender..*Controller.*(..))")
    public void logInfo(JoinPoint joinPoint) {
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        log.info("Request URL: {}, HTTP Method: {}", url, method);
    }
}
