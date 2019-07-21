package com.ts.server.ods.base.controller.form;

import com.ts.server.ods.base.domain.GradeRate;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 修改评分比例提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class GradeRateUpdateForm extends GradeRateSaveForm{
    @ApiModelProperty(value = "编号", required = true)
    @NotBlank
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public GradeRate toDomain() {
        GradeRate t = super.toDomain();
        t.setId(id);

        return t;
    }
}
