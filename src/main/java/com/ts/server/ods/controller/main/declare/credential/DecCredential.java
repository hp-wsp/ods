package com.ts.server.ods.controller.main.declare.credential;

import com.ts.server.ods.security.Credential;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * 申报端{@link Credential}
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class DecCredential extends Credential {
    private final String companyId;

    /**
     * 构造{@link DecCredential}
     *
     * @param id 用户编号
     * @param username 用户名
     * @param roles 角色集合
     * @param companyId 单位编号
     */
    public DecCredential(String id, String username, List<String> roles, String companyId) {
        super(id, username, roles);
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("companyId", companyId)
                .toString();
    }
}
