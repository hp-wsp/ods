package com.ts.server.ods.controller.vo;

/**
 * 执行是否成功
 *
 * @author WangWei
 */
public class OkVo {
    private final boolean ok;

    public OkVo(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }
}
