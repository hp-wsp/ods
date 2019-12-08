package com.ts.server.ods.base.event;

/**
 * 管理员事件
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ManagerEvent {
    /**
     * 管理员编号
     */
    private final String id;

    /**
     * 事件
     */
    private final String event;

    /**
     * 构造{@link ManagerEvent}
     *
     * @param id 管理员编号
     * @param event 事件
     */
    public ManagerEvent(String id, String event) {
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
