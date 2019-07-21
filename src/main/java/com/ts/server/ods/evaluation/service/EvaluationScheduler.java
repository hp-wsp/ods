package com.ts.server.ods.evaluation.service;

import com.ts.server.ods.evaluation.domain.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 评测定时任务处理
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaluationScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationScheduler.class);
    private final EvaluationService service;

    @Autowired
    public EvaluationScheduler(EvaluationService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = 60000L, initialDelay = 20000L)
    public void open(){
        List<Evaluation> lst = service.query("", Evaluation.Status.WAIT, 0, 50);
        long now = System.currentTimeMillis();
        lst.stream().filter(e -> isOpen(now, e))
                .peek(e -> LOGGER.info("Open evaluation id={},name={}", e.getId(), e.getName()))
                .forEach(e -> service.updateStatus(e.getId(), Evaluation.Status.OPEN, "ROBOT"));
    }

    private boolean isOpen(long now, Evaluation e){
        return e.getFromTime().getTime()<= now && e.getToTime().getTime() >= now;
    }

    @Scheduled(fixedDelay = 60000L, initialDelay = 50000L)
    public void close(){
        List<Evaluation> lst = service.query("", Evaluation.Status.OPEN, 0, 50);
        long now = System.currentTimeMillis();
        lst.stream().filter(e -> isClose(now, e))
                .peek(e -> LOGGER.info("Close evaluation id={},name={}", e.getId(), e.getName()))
                .forEach(e -> service.updateStatus(e.getId(), Evaluation.Status.CLOSE, "ROBOT"));
    }

    private boolean isClose(long now, Evaluation e){
        return e.getToTime().getTime() <= now;
    }
}