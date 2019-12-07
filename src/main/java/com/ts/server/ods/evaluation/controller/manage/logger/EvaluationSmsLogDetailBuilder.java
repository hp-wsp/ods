package com.ts.server.ods.evaluation.controller.manage.logger;

import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建评测短信日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaluationSmsLogDetailBuilder {

    /**
     * 构建打开测评日志
     */
    public final static class SendDeclareBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            String id = (String)joinPoint.getArgs()[0];
            return String.format("编号:%s", id);
        }
    }

}
