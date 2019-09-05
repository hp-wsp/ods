package com.ts.server.ods.etask.service.listener;

import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.service.event.EvaOpenDecEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 测评打开侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaOpenDecListener {
    private final TaskCardService service;

    @Autowired
    public EvaOpenDecListener(TaskCardService service) {
        this.service = service;
    }

    @EventListener
    public void listener(EvaOpenDecEvent t){
        service.openDec(t.getEvnId());
    }
}
