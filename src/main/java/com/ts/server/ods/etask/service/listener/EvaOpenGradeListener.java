package com.ts.server.ods.etask.service.listener;

import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.service.event.EvaOpenGradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 开启审核侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaOpenGradeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaOpenGradeListener.class);
    private final TaskCardService service;

    @Autowired
    public EvaOpenGradeListener(TaskCardService service) {
        this.service = service;
    }

    @EventListener
    public void listener(EvaOpenGradeEvent t){
        LOGGER.debug("Open grade listener id={}", t.getEvnId());
        service.openGrade(t.getEvnId());
    }
}
