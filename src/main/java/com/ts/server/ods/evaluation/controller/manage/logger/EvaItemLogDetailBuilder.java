package com.ts.server.ods.evaluation.controller.manage.logger;

import com.ts.server.ods.controller.form.BatchDeleteForm;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.evaluation.controller.manage.form.EvaItemUpdateForm;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建评测指标日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaItemLogDetailBuilder {

    /**
     * 构建删除评测指标日志
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
     * 构建新增评测指标日志
     */
    public static class SaveBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            ResultVo<EvaItem> result = (ResultVo<EvaItem>)joinPoint.getTarget();
            EvaItem t = result.getRs();
            return String.format("编号:%s;测评指标编号:%s", t.getId(), t.getNum());
        }
    }

    /**
     * 构建修改评测指标日志
     */
    public final static class UpdateBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            EvaItemUpdateForm form = (EvaItemUpdateForm) joinPoint.getArgs()[0];
            return String.format("编号:%s;详情:%s", form.getId(), form.toDomain().toString());
        }
    }

    /**
     * 构建批量删除评测指标日志
     */
    public final static class BatchDeleteBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            BatchDeleteForm form = (BatchDeleteForm) joinPoint.getArgs()[0];
            ResultVo<OkVo> result = (ResultVo<OkVo>)joinPoint.getTarget();
            boolean ok = result.getRs().isOk();
            return String.format("删除:%s;编号:[%s]", ok?"成功":"失败", StringUtils.join(form.getIds(), ','));
        }
    }

    /**
     * 构建导入评测指标日志
     */
    public final static class ImportBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes) {
            String evnId = (String)joinPoint.getArgs()[1];
            ResultVo<OkVo> result = (ResultVo<OkVo>)joinPoint.getTarget();
            boolean ok = result.getRs().isOk();
            return String.format("导入:%s;测评编号:%s",  ok?"成功":"失败", evnId);
        }
    }
}
