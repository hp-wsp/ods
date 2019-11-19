package com.ts.server.ods.base.controller.manage.form;

import com.ts.server.ods.base.domain.Company;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 修改公司提交数据
 *
 * @author <a href="mailto:hhywangwei@mgail.com">WangWei</a>
 */
public class CompanyUpdateForm extends CompanySaveForm {
    @NotBlank
    @ApiModelProperty(value = "编号", required = true)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Company toDomain() {
        Company t= super.toDomain();
        t.setId(id);

        return t;
    }
}
