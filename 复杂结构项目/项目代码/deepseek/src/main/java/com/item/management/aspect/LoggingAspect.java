// LoggingAspect.java - 日志切面
package com.item.management.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.item.management.controller..*.*(..))")
    public void controllerPointcut() {
        // Controller切点
    }

    @Pointcut("execution(* com.item.management.service..*.*(..))")
    public void servicePointcut() {
        // Service切点
    }

    @Before("controllerPointcut()")
    public void logBeforeController(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        log.info("=== 请求开始 ===");
        log.info("请求URL: {} {}", request.getMethod(), request.getRequestURI());
        log.info("请求IP: {}", request.getRemoteAddr());
        log.info("请求方法: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        log.info("请求参数: {}", Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        log.info("响应结果: {}", result);
        log.info("=== 请求结束 ===");
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Controller异常 - 方法: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        log.error("异常信息: {}", error.getMessage());
        log.error("异常堆栈: ", error);
    }

    @Before("servicePointcut()")
    public void logBeforeService(JoinPoint joinPoint) {
        log.debug("Service方法调用: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        log.debug("方法参数: {}", Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "servicePointcut()", returning = "result")
    public void logAfterService(JoinPoint joinPoint, Object result) {
        log.debug("Service方法返回: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        log.debug("返回结果: {}", result);
    }

    @AfterThrowing(pointcut = "servicePointcut()", throwing = "error")
    public void logAfterThrowingService(JoinPoint joinPoint, Throwable error) {
        log.error("Service异常 - 方法: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        log.error("异常信息: {}", error.getMessage());
        log.error("异常堆栈: ", error);
    }
}