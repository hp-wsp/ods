package com.ts.server.ods.base.controller.declare.logger;

import com.ts.server.ods.base.controller.manage.form.MemberUpdateForm;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.controller.form.PasswordResetForm;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建操作申报员日志
 *
 * @author <a href="hhywangwei@gmail.com">WangWei</a>
 */
public class MemberDecLogDetailBuilder {
    /**
     * 构建删除申报员日志
     */
    public final static class DeleteBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            ResultVo<OkVo> result = (ResultVo<OkVo>)returnObj;
            boolean ok = result.getRs().isOk();
            String id = (String)joinPoint.getArgs()[0];
            return String.format("删除:%s;编号:%s", ok?"成功":"失败", id);
        }
    };

    /**
     * 构建新增申报员日志
     */
    public final static class SaveBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            ResultVo<Member> result = (ResultVo<Member>)returnObj;
            Member t = result.getRs();
            return String.format("编号:%s;用户名:%s", t.getId(), t.getUsername());
        }
    }

    /**
     * 构建修改申报员日志
     */
    public final static class UpdateBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            MemberUpdateForm form = (MemberUpdateForm) joinPoint.getArgs()[0];
            return String.format("编号:%s;详情:%s", form.getId(), form.toDomain().toString());
        }
    }

    /**
     * 构建重置密码日志
     */
    public final static class ResetPasswordBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            PasswordResetForm form = (PasswordResetForm) joinPoint.getArgs()[0];
            ResultVo<OkVo> result = (ResultVo<OkVo>)joinPoint.getTarget();
            boolean ok = result.getRs().isOk();
            return String.format("重置密码:%s;用户编号:%s", ok?"成功":"失败", form.getId());
        }
    }
}
