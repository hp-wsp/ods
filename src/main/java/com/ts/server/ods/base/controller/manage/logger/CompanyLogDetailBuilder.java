package com.ts.server.ods.base.controller.manage.logger;

import com.ts.server.ods.base.controller.manage.form.CompanyUpdateForm;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.logger.aop.annotation.ApiLogDetailBuilder;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 构建单位日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class CompanyLogDetailBuilder {

    /**
     * 构建删除单位日志
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
     * 构建新增单位日志
     */
    public final static class SaveBuilder implements ApiLogDetailBuilder {
        @Override
        @SuppressWarnings("unchecked")
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            joinPoint.getSignature();
            ResultVo<Company> result = (ResultVo<Company>)returnObj;
            Company t = result.getRs();
            return String.format("编号:%s;名称:%s;分组:%s", t.getId(), t.getName(), t.getGroup());
        }
    }

    /**
     * 构建修改单位日志
     */
    public final static class UpdateBuilder implements ApiLogDetailBuilder {
        @Override
        public String build(JoinPoint joinPoint, ServletRequestAttributes attributes, Object returnObj) {
            CompanyUpdateForm form = (CompanyUpdateForm) joinPoint.getArgs()[0];
            return String.format("编号:%s;详情:%s", form.getId(), form.toDomain().toString());
        }
    }
}
