package com.ts.server.ods.controller.form;

import io.swagger.annotations.ApiModelProperty;

/**
 * 申报员修改信息
 *
 * <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class MemberInfoForm {
    @ApiModelProperty(value = "姓名")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
