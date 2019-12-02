package com.ts.server.ods.controller.main.mangae.logger;

import com.ts.server.ods.controller.main.mangae.form.ManagerInfoForm;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建管理基础日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class BaseManageLogDetailBuilder {

    /**
     * 构建修改密码日志
     */
    public static class UpdatePasswordBuilder implements ApiLogDetailBuilder{
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<OkVo> result = (ResultVo<OkVo>)joinPoint.getTarget();
            return String.format("修改密码:%s", result.getRs().isOk()? "成功": "失败");
        }
    }

    /**
     * 更新管理员信息
     */
    public static class UpdateAccountBuilder implements ApiLogDetailBuilder {

        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ManagerInfoForm form = (ManagerInfoForm)joinPoint.getArgs()[0];
            return String.format("姓名:%s;电话:%s", form.getName(), form.getPhone());
        }
    }
}
