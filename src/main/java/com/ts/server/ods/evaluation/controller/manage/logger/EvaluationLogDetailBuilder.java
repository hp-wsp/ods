package com.ts.server.ods.evaluation.controller.manage.logger;

import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.evaluation.controller.manage.form.EvaluationUpdateForm;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建评测日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaluationLogDetailBuilder {

    /**
     * 构建删除评测日志
     */
    public final static class DeleteBuilder implements ApiLogDetailBuilder {
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
     * 构建新增评测日志
     */
    public final static class SaveBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<Evaluation> result = (ResultVo<Evaluation>)joinPoint.getTarget();
            Evaluation t = result.getRs();
            return String.format("编号:%s;名称:%s", t.getId(), t.getName());
        }
    }

    /**
     * 构建修改评测日志
     */
    public final static class UpdateBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            EvaluationUpdateForm form = (EvaluationUpdateForm) joinPoint.getArgs()[0];
            return String.format("编号:%s;详情:%s", form.getId(), form.toDomain().toString());
        }
    }

    /**
     * 构建打开测评日志
     */
    public final static class OpenBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<Evaluation> result = (ResultVo<Evaluation>)joinPoint.getTarget();
            Evaluation t = result.getRs();
            return String.format("编号:%s;名称:%s", t.getId(), t.getName());
        }
    }

    /**
     * 构建打开测评日志
     */
    public final static class CloseBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<Evaluation> result = (ResultVo<Evaluation>)joinPoint.getTarget();
            Evaluation t = result.getRs();
            return String.format("编号:%s;名称:%s", t.getId(), t.getName());
        }
    }

    /**
     * 构建打开测评日志
     */
    public final static class SendSmsBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            String id = (String)joinPoint.getArgs()[0];
            return String.format("编号:%s", id);
        }
    }
}
