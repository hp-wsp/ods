package com.ts.server.ods.evaluation.service.event;

/**
 * 关闭审核事件
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaCloseGradeEvent {

    private final String evnId;

    public EvaCloseGradeEvent(String evnId) {
        this.evnId = evnId;
    }

    public String getEvnId() {
        return evnId;
    }
}
