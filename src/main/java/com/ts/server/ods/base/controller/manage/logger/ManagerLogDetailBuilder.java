package com.ts.server.ods.base.controller.manage.logger;

import com.ts.server.ods.base.controller.manage.form.ManagerUpdateForm;
import com.ts.server.ods.base.controller.manage.form.PasswordResetForm;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建管理员日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ManagerLogDetailBuilder {

    /**
     * 构建删除管理员日志
     */
    public static class DeleteBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<OkVo> result = (ResultVo<OkVo>)joinPoint.getTarget();
            boolean ok = result.getRs().isOk();
            String id = (String)joinPoint.getArgs()[0];
            return String.format("删除:%s;编号:%s", ok?"成功":"失败", id);
        }
    };

    /**
     * 构建新增管理员日志
     */
    public static class SaveBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<Manager> result = (ResultVo<Manager>)joinPoint.getTarget();
            Manager t = result.getRs();
            return String.format("编号:%s;用户名:%s", t.getId(), t.getUsername());
        }
    }

    /**
     * 构建修改管理员日志
     */
    public static class UpdateBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ManagerUpdateForm form = (ManagerUpdateForm) joinPoint.getArgs()[0];
            return String.format("编号:%s;详情:%s", form.getId(), form.toDomain().toString());
        }
    }

    /**
     * 构建重置密码日志
     */
    public static class ResetPasswordBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            PasswordResetForm form = (PasswordResetForm) joinPoint.getArgs()[0];
            ResultVo<OkVo> result = (ResultVo<OkVo>)joinPoint.getTarget();
            boolean ok = result.getRs().isOk();
            return String.format("重置密码:%s;用户编号:%s", ok?"成功":"失败", form.getId());
        }
    }
}
