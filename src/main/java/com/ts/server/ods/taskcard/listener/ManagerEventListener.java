package com.ts.server.ods.taskcard.listener;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.base.event.MemberEvent;
import com.ts.server.ods.base.service.ManagerService;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.taskcard.service.TaskCardService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 管理员事件侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class ManagerEventListener {
    private final EvaluationService evaService;
    private final TaskCardService cardService;
    private final ManagerService managerService;

    @Autowired
    public ManagerEventListener(EvaluationService evaService, TaskCardService cardService,
                                ManagerService managerService) {

        this.evaService = evaService;
        this.cardService = cardService;
        this.managerService = managerService;
    }

    @EventListener
    public void listener(MemberEvent event){
        if(isUpdate(event.getEvent())){
            updateDecMember(event.getId());
        }

        if (isDelete(event.getEvent())) {
            validateDelete(event.getId());
        }
    }

    private boolean isUpdate(String event){
        return StringUtils.equals(event, "update");
    }

    private void updateDecMember(String id){
        Manager t = managerService.get(id);
        evaService.queryActive().forEach(e -> cardService.updateAssManager(e.getId(), t));
    }

    private boolean isDelete(String event){
        return StringUtils.equals(event, "delete");
    }

    private void validateDelete(String id){
        boolean has = evaService.queryActive().stream().anyMatch(e -> cardService.hasAssManagerByEvaId(e.getId(), id));
        if(has){
            throw new BaseException("测评员已经分配，不能删除");
        }
    }
}
