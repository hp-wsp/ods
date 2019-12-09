package com.ts.server.ods.taskcard.controller.declare.logger;

import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.taskcard.domain.Declaration;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建资源日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ResourceDecLogDetailBuilder {

    /**
     * 构建新增资源日志
     */
    public final static class SaveBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            ResultVo<Declaration> result = (ResultVo<Declaration>)returnObj;
            Declaration t = result.getRs();
            return String.format("编号:%s;文件:%s", t.getId(), t.getFileName());
        }
    }

    /**
     * 构建删除资源日志
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
}
