package com.ts.server.ods.controller.main.logger;

import com.ts.server.ods.common.utils.HttpUtils;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建登录日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class LoginLogDetailBuilder implements ApiLogDetailBuilder {

    @Override
    public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
        return String.format("IP:%s", HttpUtils.getHostname(attributes.getRequest()));
    }
}
