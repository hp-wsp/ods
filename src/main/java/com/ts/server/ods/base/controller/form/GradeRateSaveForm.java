package com.ts.server.ods.base.controller.form;

import com.ts.server.ods.base.domain.GradeRate;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 新增评分比例提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class GradeRateSaveForm {
    @ApiModelProperty(value = "级别", required =  true)
    @NotBlank
    private String level;
    @ApiModelProperty(value = "评分比例", required = true)
    @Min(0) @Max(100)
    private int rate;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public GradeRate toDomain(){
        GradeRate t = new GradeRate();

        t.setLevel(level);
        t.setRate(rate);

        return t;
    }
}
