package com.ts.server.ods.controller.main.mangae.logger;

import com.ts.server.ods.controller.main.mangae.form.ManagerInfoForm;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 更新管理员信息
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class UpdateMangerAccountLogDetailBuilder implements ApiLogDetailBuilder {

    @Override
    public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
        ManagerInfoForm form = (ManagerInfoForm)joinPoint.getArgs()[0];
        return String.format("姓名:%s;电话:%s", form.getName(), form.getPhone());
    }
}
