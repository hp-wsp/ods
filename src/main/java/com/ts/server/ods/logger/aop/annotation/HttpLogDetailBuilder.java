package com.ts.server.ods.logger.aop.annotation;

import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

public interface HttpLogDetailBuilder {

    String build(JoinPoint joinPoint, ServletRequestAttributes attributes);
}
