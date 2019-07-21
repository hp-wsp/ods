package com.ts.server.ods.evaluation.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Objects;

/**
 * 测评
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class Evaluation {
    @ApiModelProperty(value = "编号")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "考核说明")
    private String remark;
    @ApiModelProperty(value = "开始时间")
    private Date fromTime;
    @ApiModelProperty(value = "结束时间")
    private Date toTime;
    @ApiModelProperty(value = "状态")
    private Status status;
    @ApiModelProperty(value = "是否导出")
    private boolean export;
    @ApiModelProperty(value = "导出任务编号")
    private String exportId;
    @ApiModelProperty(value = "是否发送申报开始通知短信")
    private boolean sms;
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public enum Status {
        WAIT, OPEN, CLOSE
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }

    public String getExportId() {
        return exportId;
    }

    public void setExportId(String exportId) {
        this.exportId = exportId;
    }

    public boolean isSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
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
        Evaluation that = (Evaluation) o;
        return export == that.export &&
                sms == that.sms &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(fromTime, that.fromTime) &&
                Objects.equals(toTime, that.toTime) &&
                status == that.status &&
                Objects.equals(exportId, that.exportId) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, remark, fromTime, toTime, status, export, exportId, sms, updateTime, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("remark", remark)
                .append("fromTime", fromTime)
                .append("toTime", toTime)
                .append("status", status)
                .append("updateExport", export)
                .append("exportId", exportId)
                .append("sms", sms)
                .append("updateTime", updateTime)
                .append("createTime", createTime)
                .toString();
    }
}

