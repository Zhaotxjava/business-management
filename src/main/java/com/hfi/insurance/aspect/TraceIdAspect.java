package com.hfi.insurance.aspect;

import com.hfi.insurance.aspect.anno.TraceId;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;

import java.util.UUID;


@Aspect
public class TraceIdAspect {
    private static final String UNIQUE_ID = "logTraceID";

    @Pointcut(value = "@annotation(com.hfi.insurance.aspect.anno.TraceId)")
    private void traceId(){

    }

    @Around(value = "traceId() && @annotation(anno)")
    public Object around(ProceedingJoinPoint point, TraceId anno) throws Throwable {
        String traceId = MDC.get(UNIQUE_ID);
        if (StringUtils.isBlank(traceId)){
            //原本不存在traceid, 则生成一个
            UUID uuid = UUID.randomUUID();
            String uniqueId = uuid.toString().replace("-", "");
            try{
                MDC.put(UNIQUE_ID, uniqueId);
                return point.proceed();
            }finally {
                //在本切面生成的traceid必须去除,防止内存溢出
                MDC.remove(UNIQUE_ID);
            }
        }else {
            //原本就存在traceid,可直接调用方法
            return point.proceed();
        }
    }
}
