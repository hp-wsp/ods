package com.ts.server.ods.base.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Objects;

/**
 * 单位申报人员
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class Member {
    @ApiModelProperty(value = "编号")
    private String id;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "联系电话", required = true)
    @NotBlank
    private String phone;
    @ApiModelProperty(value = "登录密码")
    @JsonIgnore
    private String password;
    @ApiModelProperty(value = "单位编号")
    private String companyId;
    @ApiModelProperty(value = "单位名称")
    private String companyName;
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id) &&
                Objects.equals(username, member.username) &&
                Objects.equals(name, member.name) &&
                Objects.equals(phone, member.phone) &&
                Objects.equals(password, member.password) &&
                Objects.equals(companyId, member.companyId) &&
                Objects.equals(companyName, member.companyName) &&
                Objects.equals(updateTime, member.updateTime) &&
                Objects.equals(createTime, member.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, phone, password, companyId, companyName, updateTime, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("username", username)
                .append("name", name)
                .append("phone", phone)
                .append("password", password)
                .append("companyId", companyId)
                .append("companyName", companyName)
                .append("updateTime", updateTime)
                .append("createTime", createTime)
                .toString();
    }
}
