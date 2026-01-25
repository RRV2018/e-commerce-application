package com.omsoft.retail.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String url = request.getRequestURI();
        String method = request.getMethod();

        long start = System.currentTimeMillis();

        log.info("➡️ REQUEST [{}] {}", method, url);

        Object response;
        try {
            response = joinPoint.proceed();
            return response;
        } finally {
            long time = System.currentTimeMillis() - start;
            log.info("⬅️ RESPONSE [{}] {} took {} ms", method, url, time);
        }
    }

}
