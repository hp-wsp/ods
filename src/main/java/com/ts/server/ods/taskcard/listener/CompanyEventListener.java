package com.ts.server.ods.taskcard.listener;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.event.CompanyEvent;
import com.ts.server.ods.base.service.CompanyService;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.taskcard.service.TaskCardService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 单位事件侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class CompanyEventListener {
    private final CompanyService companyService;
    private final EvaluationService evaService;
    private final TaskCardService cardService;

    @Autowired
    public CompanyEventListener(EvaluationService evaService, TaskCardService cardService,
                                CompanyService companyService) {

        this.evaService = evaService;
        this.cardService = cardService;
        this.companyService = companyService;
    }

    @EventListener
    public void listen(CompanyEvent event){
        if(isUpdate(event.getEvent())){
            update(event.getId());
        }

        if(isDelete(event.getEvent())){
            validateDelete(event.getId());
        }
    }

    private boolean isUpdate(String event){
        return StringUtils.equals(event, "update");
    }

    private void update(String id){
        Company t = companyService.get(id);
        evaService.queryActive().forEach(e -> cardService.updateCompany(e.getId(), t));
    }

    private boolean isDelete(String event){
        return StringUtils.equals(event, "delete");
    }

    private void validateDelete(String id){
        boolean has = evaService.queryActive().stream().anyMatch(e -> cardService.hasCompanyByEvaId(e.getId(), id));
        if(has){
            throw new BaseException("已经分配测评任务，不能删除");
        }
    }
}
