package com.zubova.library.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class PerformanceLoggingAspect {

    @Around("@annotation(com.zubova.library.common.aop.TrackExecutionTime)")
    public Object log(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.nanoTime();
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            long totalTime = System.nanoTime() - startTime;
            log.info("{}.{} executed in {} ms",
                    proceedingJoinPoint.getTarget().getClass().getSimpleName(),
                    proceedingJoinPoint.getSignature().getName(),
                    totalTime / 1_000_000);
        }
    }

}