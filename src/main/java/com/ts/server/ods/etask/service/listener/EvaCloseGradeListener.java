package com.ts.server.ods.etask.service.listener;

import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.service.event.EvaCloseGradeEvent;
import com.ts.server.ods.evaluation.service.event.EvaOpenGradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 关闭审核侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaCloseGradeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaCloseGradeListener.class);
    private final TaskCardService service;

    @Autowired
    public EvaCloseGradeListener(TaskCardService service) {
        this.service = service;
    }

    @EventListener
    public void listener(EvaCloseGradeEvent t){
        LOGGER.debug("Close grade listener id={}", t.getEvnId());
        service.closeGrade(t.getEvnId());
    }
}
