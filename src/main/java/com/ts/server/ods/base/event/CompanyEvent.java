package com.ts.server.ods.base.event;

/**
 * 单位事件
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class CompanyEvent {
    private final String id;
    private final String event;

    /**
     * 构造{@link CompanyEvent}
     *
     * @param id 编号
     * @param event 事件
     */
    public CompanyEvent(String id, String event) {
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
