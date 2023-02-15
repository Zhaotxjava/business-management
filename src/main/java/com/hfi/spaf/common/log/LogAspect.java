package com.hfi.spaf.common.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Aspect
@Order(1)
@Component
@Slf4j
public class LogAspect {

    private ObjectMapper objectMapper;

    public LogAspect() {
        objectMapper = new ObjectMapper();
    }

    @Pointcut("execution(* com.hfi.spaf.controller..*(..)) ")
    private void log(){

    }

    @Around(value = "log()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //含有调用类, 方法, 入参的sb
        StringBuilder info = getInvokeInfo(point, true);
        log.info(info.toString());
        long startTime = System.currentTimeMillis();
        //有调用类, 方法, 没有入参的sb
        info = getInvokeInfo(point, false);
        Object result = point.proceed();
        long endTime = System.currentTimeMillis();
        info.append(" 本地接口出参:{} cost time = {} ms");
        log.info(info.toString(), objectMapper.writeValueAsString(result), (endTime - startTime));
        return result;
    }

    @AfterThrowing(value = "log()", throwing = "ex")
    public void afterThrowing(JoinPoint point, Exception ex) throws JsonProcessingException {
        StringBuilder sb = getInvokeInfo(point, true)
                .append("异常结果:");
        log.error(sb.toString(), ex);
    }

    private StringBuilder getInvokeInfo(JoinPoint point, boolean withInput) throws JsonProcessingException {
        MethodSignature signature = (MethodSignature) point.getSignature();
        StringBuilder info = new StringBuilder()
                .append("\"className\":\"")
                .append(signature.getDeclaringType().getSimpleName())
                .append("\" \"methodName\":\"")
                .append(signature.getName())
                .append("\"");
        if (withInput){
            info.append(" 本地接口入参:");
            int count = 0;
            for (Object item : point.getArgs()) {
                if(item instanceof ServletRequest || item instanceof ServletResponse  || item instanceof MultipartFile){
                    continue;
                }
                if (count++ == 0) {
                    info.append(objectMapper.writeValueAsString(item));
                } else {
                    info.append(";\t").append(objectMapper.writeValueAsString(item));
                }
            }
        }
        return info;
    }
}
