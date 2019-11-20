package com.ts.server.ods.logger.aop.annotation;

import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 不获取用户名
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class NoneObtainUsername implements ObtainUsername {

    @Override
    public String obtain(JoinPoint joinPoint, ServletRequestAttributes attributes) {
        return "";
    }
}
