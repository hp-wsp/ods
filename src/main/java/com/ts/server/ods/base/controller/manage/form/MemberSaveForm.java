package com.ts.server.ods.base.controller.manage.form;

import com.ts.server.ods.base.domain.Member;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 *  新增申报员提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class MemberSaveForm {
    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank
    private String username;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "联系电话", required = true)
    @NotBlank
    private String phone;
    @ApiModelProperty(value = "登录密码", required = true)
    @NotBlank
    private String password;
    @ApiModelProperty(value = "单位编号", required = true)
    @NotBlank
    private String companyId;
    @ApiModelProperty(value = "是否为单位管理员", required = true)
    private boolean manager;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public Member toDomain(){
        Member t = new Member();

        t.setUsername(username);
        t.setName(name);
        t.setPhone(phone);
        t.setPassword(password);
        t.setCompanyId(companyId);
        t.setManager(manager);

        return t;
    }
}
