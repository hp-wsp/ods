package com.ts.server.ods.evaluation.event;

/**
 * 测评事件
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaluationEvent {

    /**
     * 测评编号
     */
    private final String id;

    /**
     * 事件
     */
    private final String event;

    /**
     * 构造{@link EvaluationEvent}
     *
     * @param id 测评编号
     * @param event 事件
     */
    public EvaluationEvent(String id, String event) {
        this.id = id;
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }
}
