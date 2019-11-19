package com.ts.server.ods.evaluation.controller.manage.form;

import com.ts.server.ods.evaluation.domain.EvaItem;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 修改指标提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaItemUpdateForm {
    @ApiModelProperty(value = "编号", required = true)
    @NotBlank
    private String id;
    @ApiModelProperty(value = "指标编号", required = true)
    @NotBlank
    private String num;
    @ApiModelProperty(value = "具体要求", required = true)
    @NotBlank
    private String require;
    @ApiModelProperty(value = "评分标准", required = true)
    @NotBlank
    private String grade;
    @ApiModelProperty(value = "结果集合", required = true)
    @NotEmpty
    private String[] results;
    @ApiModelProperty(value = "说明")
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String[] getResults() {
        return results;
    }

    public void setResults(String[] results) {
        this.results = results;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public EvaItem toDomain(){
        EvaItem t = new EvaItem();

        t.setId(id);
        t.setNum(num);
        t.setRequire(require);
        t.setGrade(grade);
        t.setResults(results);
        t.setRemark(remark);

        return t;
    }
}
