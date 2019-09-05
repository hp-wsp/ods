package com.ts.server.ods.evaluation.service.event;

/**
 * 关闭测评事件
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaCloseDecEvent {
    /**
     * 测评编号
     */
    private final String evnId;

    public EvaCloseDecEvent(String evnId) {
        this.evnId = evnId;
    }

    public String getEvnId() {
        return evnId;
    }
}
