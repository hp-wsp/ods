package com.ts.server.ods.evaluation.controller.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ts.server.ods.BaseException;
import com.ts.server.ods.evaluation.domain.Evaluation;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 新增测评提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaluationSaveForm {
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
    @ApiModelProperty(value = "导入评测编号")
    private String importId;
    @ApiModelProperty(value = "是否导入测评任务")
    private boolean importTask = false;

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

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public boolean isImportTask() {
        return importTask;
    }

    public void setImportTask(boolean importTask) {
        this.importTask = importTask;
    }

    public Evaluation toDomain(){
        Evaluation t = new Evaluation();

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
