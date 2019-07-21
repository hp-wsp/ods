package com.ts.server.ods.etask.service.listener;

import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.service.event.EvaCloseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 测评关闭侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaluationCloseListener {
    private final TaskCardService service;

    @Autowired
    public EvaluationCloseListener(TaskCardService service) {
        this.service = service;
    }

    @EventListener
    public void listener(EvaCloseEvent t){
        service.close(t.getEvnId());
    }
}
