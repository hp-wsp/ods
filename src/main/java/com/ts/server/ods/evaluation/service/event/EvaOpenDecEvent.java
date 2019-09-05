package com.ts.server.ods.evaluation.service.event;

/**
 * 打开测评事件
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaOpenDecEvent {
    /**
     * 测评编号
     */
    private final String evnId;

    public EvaOpenDecEvent(String evnId) {
        this.evnId = evnId;
    }

    public String getEvnId() {
        return evnId;
    }
}
