package com.ts.server.ods.controller.main.declare.logger;

import com.ts.server.ods.controller.main.declare.form.MemberInfoForm;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 更新申报员信息
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class UpdateMemberAccountLogDetailBuilder implements ApiLogDetailBuilder {

    @Override
    public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
        MemberInfoForm form = (MemberInfoForm)joinPoint.getArgs()[0];
        return String.format("姓名:%s", form.getName());
    }
}
