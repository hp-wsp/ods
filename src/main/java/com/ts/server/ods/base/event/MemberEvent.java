package com.ts.server.ods.base.event;

/**
 * 申报员事件
 *
 * @author <a href="mailto:hhywangwei@gamil.com">WangWei</a>
 */
public class MemberEvent {
    /**
     * 申报员编号
     */
    private final String id;
    /**
     * 事件
     */
    private final String event;

    /**
     * 构造{@link MemberEvent}
     *
     * @param id 申报员编号
     * @param event 事件
     */
    public MemberEvent(String id, String event) {
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
