package com.ts.server.ods.etask.service.listener;

import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.service.event.EvaOpenEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 测评打开侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaluationOpenListener {
    private final TaskCardService service;

    @Autowired
    public EvaluationOpenListener(TaskCardService service) {
        this.service = service;
    }

    @EventListener
    public void listener(EvaOpenEvent t){
        service.open(t.getEvnId());
    }
}
