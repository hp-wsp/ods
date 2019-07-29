package com.ts.server.ods.base.controller.form;

import com.ts.server.ods.base.domain.Company;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * 新增公司提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class CompanySaveForm {
    @NotBlank
    @ApiModelProperty(value = "公司名称", required = true)
    private String name;
    @NotBlank
    @ApiModelProperty(value = "申报员电话", required = true)
    private String phone;
    @ApiModelProperty(value = "申报员姓名")
    private String contact;
    @ApiModelProperty(value = "分组")
    private String group;

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

    public Company toDomain(){
        Company t = new Company();

        t.setName(name);
        t.setPhone(phone);
        t.setContact(contact);
        t.setGroup(group);

        return t;
    }
}
