package com.ts.server.ods.etask.controller.form;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 打分提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class GradeForm {
    @ApiModelProperty(value = "评测指标编号", required = true)
    @NotBlank
    private String id;
    @ApiModelProperty(value = "评分级别", required = true)
    @NotBlank
    private String level;
    @ApiModelProperty(value = "分数", required = true)
    @NotNull
    private int score;
    @ApiModelProperty(value = "评分备注")
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
