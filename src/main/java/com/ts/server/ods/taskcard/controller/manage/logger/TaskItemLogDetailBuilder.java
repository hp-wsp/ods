package com.ts.server.ods.taskcard.controller.manage.logger;

import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.taskcard.controller.manage.form.TaskItemUpdateForm;
import com.ts.server.ods.taskcard.domain.TaskCardItem;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建测评卡项目日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskItemLogDetailBuilder {
    /**
     * 构建删除评卡项目日志
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
     * 构建新增评卡项目日志
     */
    public final static class SaveBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<TaskCardItem> result = (ResultVo<TaskCardItem>)joinPoint.getTarget();
            TaskCardItem t = result.getRs();
            return String.format("编号:%s;测评指标编号:%s", t.getId(), t.getEvaNum());
        }
    }

    /**
     * 构建修改评卡项目日志
     */
    public final static class UpdateBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            TaskItemUpdateForm form = (TaskItemUpdateForm) joinPoint.getArgs()[0];
            return String.format("编号:%s;详情:%s", form.getId(), form.toDomain().toString());
        }
    }

    /**
     * 构建导入评卡项目日志
     */
    public final static class ImportBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            String cardId = (String) joinPoint.getArgs()[1];
            return String.format("测评卡编号:%s", cardId);
        }
    }
}
