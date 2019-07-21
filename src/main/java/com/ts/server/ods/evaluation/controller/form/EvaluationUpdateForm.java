package com.ts.server.ods.evaluation.controller.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ts.server.ods.BaseException;
import com.ts.server.ods.evaluation.domain.Evaluation;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 修改测评
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaluationUpdateForm {
    @ApiModelProperty(value = "编号", required = true)
    @NotBlank
    private String id;
    @ApiModelProperty(value = "名称",required = true)
    @NotBlank
    private String name;
    @ApiModelProperty("考核说明")
    private String remark;
    @ApiModelProperty(value = "开始时间", required = true)
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fromTime;
    @ApiModelProperty(value = "结束时间", required = true)
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date toTime;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    public Evaluation toDomain(){
        Evaluation t = new Evaluation();

        t.setId(id);
        t.setName(name);
        t.setRemark(remark);
        t.setFromTime(fromTime);
        t.setToTime(toTime);

        if(t.getFromTime().getTime() > t.getToTime().getTime()){
            throw new BaseException("开始时间不能大于结束时间");
        }
        return t;
    }
}
