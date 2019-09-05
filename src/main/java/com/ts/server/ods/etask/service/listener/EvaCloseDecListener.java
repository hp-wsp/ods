package com.ts.server.ods.etask.service.listener;

import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.service.event.EvaCloseDecEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 测评关闭侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaCloseDecListener {
    private final TaskCardService service;

    @Autowired
    public EvaCloseDecListener(TaskCardService service) {
        this.service = service;
    }

    @EventListener
    public void listener(EvaCloseDecEvent t){
        service.closeDec(t.getEvnId());
    }
}
