package com.ts.server.ods.logger.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * 操作日志对象
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class OptLog {
    @ApiModelProperty("编号")
    private long id;
    @ApiModelProperty("详情")
    private String detail;
    @ApiModelProperty("参数")
    private String params;
    @ApiModelProperty("操作用户名")
    private String username;
    @ApiModelProperty("操作日志")
    private Date createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
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
        OptLog optLog = (OptLog) o;
        return id == optLog.id &&
                Objects.equals(detail, optLog.detail) &&
                Objects.equals(params, optLog.params) &&
                Objects.equals(username, optLog.username) &&
                Objects.equals(createTime, optLog.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, detail, params, username, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("detail", detail)
                .append("params", params)
                .append("username", username)
                .append("createTime", createTime)
                .toString();
    }
}
