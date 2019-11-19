package com.ts.server.ods.logger.aop;

import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;

public class HttpLoggerAspect {

    public void beforeLog(JoinPoint joinPoint){
        //joinPoint.getSignature().getDeclaringType().getAnnotations()
    }
}
