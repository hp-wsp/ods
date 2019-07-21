package com.ts.server.ods.base.controller.form;

import com.ts.server.ods.base.domain.Manager;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改管理员提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ManagerUpdateForm {
    @ApiModelProperty(value = "用户编号")
    @NotBlank
    private String id;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "联系电话")
    private String phone;
    @ApiModelProperty(value = "电子邮件")
    private String email;
    @ApiModelProperty(value = "用户角色")
    @NotBlank
    private String role;
    @ApiModelProperty(value = "是否禁用")
    @NotNull
    private Boolean forbid = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getForbid() {
        return forbid;
    }

    public void setForbid(Boolean forbid) {
        this.forbid = forbid;
    }

    public Manager toDomain(){
        Manager t = new Manager();

        t.setId(id);
        t.setName(name);
        t.setPhone(phone);
        t.setEmail(email);
        t.setRole(role);
        t.setForbid(forbid);

        return t;
    }
}
