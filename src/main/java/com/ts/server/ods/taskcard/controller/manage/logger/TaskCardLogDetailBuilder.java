package com.ts.server.ods.taskcard.controller.manage.logger;

import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.taskcard.controller.manage.form.TaskCardUpdateForm;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建测评卡日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskCardLogDetailBuilder {

    /**
     * 构建删除测评卡日志
     */
    public final static class DeleteBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            ResultVo<OkVo> result = (ResultVo<OkVo>)joinPoint.getTarget();
            boolean ok = result.getRs().isOk();
            String id = (String)joinPoint.getArgs()[0];
            return String.format("删除:%s;编号:%s", ok?"成功":"失败", id);
        }
    };

    /**
     * 构建新增评卡日志
     */
    public final static class SaveBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            ResultVo<TaskCard> result = (ResultVo<TaskCard>)joinPoint.getTarget();
            TaskCard t = result.getRs();
            return String.format("编号:%s;测评:%s;单位:%s", t.getId(), t.getEvaName(), t.getCompanyName());
        }
    }

    /**
     * 构建修改评卡日志
     */
    public final static class UpdateBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            TaskCardUpdateForm form = (TaskCardUpdateForm) joinPoint.getArgs()[0];
            return String.format("编号:%s;详情:%s", form.getId(), form.toDomain().toString());
        }
    }
}
