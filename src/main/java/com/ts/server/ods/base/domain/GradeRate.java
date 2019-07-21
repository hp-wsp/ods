package com.ts.server.ods.base.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * 评分比例
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class GradeRate {
    @ApiModelProperty("编号")
    private String id;
    @ApiModelProperty("级别")
    private String level;
    @ApiModelProperty("得分比例")
    private int rate;
    @ApiModelProperty("修改日志")
    private Date updateTime;
    @ApiModelProperty("创建日期")
    private Date createTime;

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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
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
        GradeRate gradeRate = (GradeRate) o;
        return rate == gradeRate.rate &&
                Objects.equals(id, gradeRate.id) &&
                Objects.equals(level, gradeRate.level) &&
                Objects.equals(updateTime, gradeRate.updateTime) &&
                Objects.equals(createTime, gradeRate.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, level, rate, updateTime, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("level", level)
                .append("rate", rate)
                .append("updateTime", updateTime)
                .append("createTime", createTime)
                .toString();
    }
}
