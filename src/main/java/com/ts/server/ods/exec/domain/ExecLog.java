package com.ts.server.ods.exec.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * 执行任务日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ExecLog {
    @ApiModelProperty("编号")
    private String id;
    @ApiModelProperty("任务Key")
    private String taskKey;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("状态")
    private Status status;
    @ApiModelProperty("错误信息")
    private String errMsg;
    @ApiModelProperty("开始时间")
    private Date fromTime;
    @ApiModelProperty("结束时间")
    private Date toTime;

    public enum Status {
        RUNNING, SUCCESS, FAIL
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecLog execLog = (ExecLog) o;
        return Objects.equals(id, execLog.id) &&
                Objects.equals(taskKey, execLog.taskKey) &&
                Objects.equals(remark, execLog.remark) &&
                status == execLog.status &&
                Objects.equals(errMsg, execLog.errMsg) &&
                Objects.equals(fromTime, execLog.fromTime) &&
                Objects.equals(toTime, execLog.toTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskKey, remark, status, errMsg, fromTime, toTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("taskKey", taskKey)
                .append("remark", remark)
                .append("status", status)
                .append("errMsg", errMsg)
                .append("fromTime", fromTime)
                .append("toTime", toTime)
                .toString();
    }
}
