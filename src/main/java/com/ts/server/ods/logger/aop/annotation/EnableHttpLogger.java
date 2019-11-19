package com.ts.server.ods.logger.aop.annotation;

import java.lang.annotation.*;

/**
 * 日志注解
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface EnableHttpLogger {

    String name();

    Class<? extends HttpLogDetailBuilder> buildDetail() default NoneLogDetailBuilder.class;
}
