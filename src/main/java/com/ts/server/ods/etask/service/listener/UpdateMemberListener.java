package com.ts.server.ods.etask.service.listener;

import com.ts.server.ods.base.service.event.UpdateMemberEvent;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 单位更新申报人员侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class UpdateMemberListener {

    private final EvaluationService evaluationService;
    private final TaskCardService cardService;

    @Autowired
    public UpdateMemberListener(EvaluationService evaluationService, TaskCardService cardService) {
        this.evaluationService = evaluationService;
        this.cardService = cardService;
    }

    @EventListener
    public void listener(UpdateMemberEvent event){
        List<Evaluation> evaluations = evaluationService.queryActive();
        evaluations.forEach(e -> cardService.updateDec(e.getId(), event.getCompanyId(), event.getMember()));
    }
}
