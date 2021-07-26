package com.hfi.insurance.aspect;
/**
 * @Auther jthealth-NZH
 * @Date 2018/7/27 10:33
 * @Describe
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;


@Aspect
public class LogAspect {

    private static final Logger log = HfiLogger.create(LogAspect.class);

    private ObjectMapper objectMapper;

    public LogAspect() {
        objectMapper = new ObjectMapper();
    }

    @Pointcut(value = "@annotation(com.hfi.insurance.aspect.anno.LogAnnotation)")
    private void log(){

    }

    @Around(value = "log() && @annotation(logAnno)")
    public Object around(ProceedingJoinPoint point, LogAnnotation logAnno) throws Throwable {
        Signature sig = point.getSignature();
        // 该注解只能用于方法
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        //含有调用类, 方法, 入参的sb
        StringBuilder info = getInvokeInfo(point, true);
        log.info(info.toString());
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long endTime = System.currentTimeMillis();
        //有调用类, 方法, 没有入参的sb
        info = getInvokeInfo(point, false);
        if (logAnno != null && logAnno.simpleLog()){
            info.append("-出参:省略 - cost time = {} ms");
            log.info(info.toString(), (endTime - startTime));
        }else {
            info.append("-出参:{} - cost time = {} ms");
            log.info(info.toString(), objectMapper.writeValueAsString(result), (endTime - startTime));
        }
        return result;
    }

    @AfterThrowing(value = "log()", throwing = "ex")
    public void afterThrowing(JoinPoint point, Exception ex) throws JsonProcessingException {
        StringBuilder sb = getInvokeInfo(point, true)
                .append("-异常结果:");
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
            info.append("-入参:");
            int count = 0;
            for (Object item : point.getArgs()) {
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
