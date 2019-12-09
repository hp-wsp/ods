package com.ts.server.ods.taskcard.controller.manage.logger;

import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.taskcard.domain.TaskCardItem;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建评分日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class GradeLogDetailBuilder {

    /**
     * 构建评分日志
     */
    public final static class GradeBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            ResultVo<TaskCardItem> result = (ResultVo<TaskCardItem>)joinPoint.getTarget();
            TaskCardItem t = result.getRs();
            return String.format("编号:%s;测评指标编号:%s;分等级:%s", t.getId(), t.getEvaNum(), t.getGradeLevel());
        }
    }

    /**
     * 构建退回日志
     */
    public static class BackBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            ResultVo<TaskCard> result = (ResultVo<TaskCard>)joinPoint.getTarget();
            TaskCard t = result.getRs();
            return String.format("编号:%s;单位:%s", t.getId(), t.getCompanyName());
        }
    }

    /**
     * 构建清除退回日志
     */
    public final static class CancelBackBuilder extends BackBuilder{
        //none instance
    }

    /**
     * 构建完成日志
     */
    public final static class FinishBuilder extends BackBuilder{
        //none instance
    }

}
