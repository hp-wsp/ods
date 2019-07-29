package com.ts.server.ods.base.service.event;

import com.ts.server.ods.base.domain.Member;

/**
 * 单位修改申报员事件
 *
 * @author <a href="hhywangwei@gamil.com">WangWei</a>
 */
public class UpdateMemberEvent {
    private final String companyId;
    private final Member member;

    public UpdateMemberEvent(String companyId, Member member) {
        this.companyId = companyId;
        this.member = member;
    }

    public String getCompanyId() {
        return companyId;
    }

    public Member getMember() {
        return member;
    }
}
