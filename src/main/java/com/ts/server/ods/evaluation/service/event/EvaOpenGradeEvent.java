package com.ts.server.ods.evaluation.service.event;

/**
 * 开启审核事件
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaOpenGradeEvent {
    private final String evnId;

    public EvaOpenGradeEvent(String evnId) {
        this.evnId = evnId;
    }

    public String getEvnId() {
        return evnId;
    }
}
