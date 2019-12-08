package com.ts.server.ods.taskcard.listener;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.evaluation.event.EvaluationEvent;
import com.ts.server.ods.taskcard.service.TaskCardService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 测评项目事件侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaluationEventListener {
    private final EvaluationService evaService;
    private final TaskCardService cardService;

    @Autowired
    public EvaluationEventListener(EvaluationService evaService, TaskCardService cardService) {
        this.evaService = evaService;
        this.cardService = cardService;
    }

    @EventListener
    public void listener(EvaluationEvent event){

        if(isDelete(event.getEvent()) && cardService.hasByEvaId(event.getId())){
            throw new BaseException("已经分配测评任务，不能删除");
        }

        Evaluation t = evaService.get(event.getId());
        cardService.updateEvaluation(t);
    }

    private boolean isDelete(String event){
        return StringUtils.equals(event, "delete");
    }
}
