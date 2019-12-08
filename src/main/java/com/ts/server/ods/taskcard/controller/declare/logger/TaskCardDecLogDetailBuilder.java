package com.ts.server.ods.taskcard.controller.declare.logger;

import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建申报测评卡日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskCardDecLogDetailBuilder {

    /**
     * 构建提交申报日志
     */
    public final static class SubmitBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<TaskCard> result = (ResultVo<TaskCard>)joinPoint.getTarget();
            TaskCard t = result.getRs();
            return String.format("编号:%s;单位:%s", t.getId(), t.getCompanyName());
        }
    }
}
