package com.ts.server.ods.base.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * 单位
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class Company {
    @ApiModelProperty(value = "编号")
    private String id;
    @ApiModelProperty(value = "单位名称")
    private String name;
    @ApiModelProperty(value = "联系电话")
    private String phone;
    @ApiModelProperty(value = "联系人")
    private String contact;
    @ApiModelProperty(value = "分组")
    private String group;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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
        Company company = (Company) o;
        return Objects.equals(id, company.id) &&
                Objects.equals(name, company.name) &&
                Objects.equals(phone, company.phone) &&
                Objects.equals(contact, company.contact) &&
                Objects.equals(group, company.group) &&
                Objects.equals(updateTime, company.updateTime) &&
                Objects.equals(createTime, company.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, contact, group, updateTime, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("phone", phone)
                .append("contact", contact)
                .append("group", group)
                .append("updateTime", updateTime)
                .append("createTime", createTime)
                .toString();
    }
}
