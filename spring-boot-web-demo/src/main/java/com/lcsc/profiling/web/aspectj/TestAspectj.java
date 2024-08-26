package com.lcsc.profiling.web.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TestAspectj {

    @Around("@annotation(com.lcsc.profiling.web.annotation.Test) || execution(* com.lcsc..*.*(..)) || @within(com.lcsc.profiling.web.annotation.Test)")
    public Object processed(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

}
