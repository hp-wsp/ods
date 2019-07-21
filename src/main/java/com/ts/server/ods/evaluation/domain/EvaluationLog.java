package com.ts.server.ods.evaluation.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * 评测任务操作日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaluationLog {
    @ApiModelProperty("编号")
    private String id;
    @ApiModelProperty("测评编号")
    private String evaId;
    @ApiModelProperty("天")
    private String day;
    @ApiModelProperty("详情")
    private String detail;
    @ApiModelProperty("操作用户")
    private String username;
    @ApiModelProperty("创建时间")
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvaId() {
        return evaId;
    }

    public void setEvaId(String evaId) {
        this.evaId = evaId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        EvaluationLog that = (EvaluationLog) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(evaId, that.evaId) &&
                Objects.equals(day, that.day) &&
                Objects.equals(detail, that.detail) &&
                Objects.equals(username, that.username) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, evaId, day, detail, username, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("evaId", evaId)
                .append("day", day)
                .append("detail", detail)
                .append("username", username)
                .append("createTime", createTime)
                .toString();
    }
}
