package com.ts.server.ods.evaluation.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 测评指标
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaItem {
    @ApiModelProperty("编号")
    private String id;
    @ApiModelProperty("测评编号")
    private String evaId;
    @ApiModelProperty("指标编号")
    private String num;
    @ApiModelProperty("具体要求")
    private String require;
    @ApiModelProperty("评分标准")
    private String grade;
    @ApiModelProperty("结果集合")
    private String[] results;
    @ApiModelProperty("说明")
    private String remark;
    @ApiModelProperty("修改时间")
    private Date updateTime;
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
        EvaItem evaItem = (EvaItem) o;
        return Objects.equals(id, evaItem.id) &&
                Objects.equals(evaId, evaItem.evaId) &&
                Objects.equals(num, evaItem.num) &&
                Objects.equals(require, evaItem.require) &&
                Objects.equals(grade, evaItem.grade) &&
                Arrays.equals(results, evaItem.results) &&
                Objects.equals(remark, evaItem.remark) &&
                Objects.equals(updateTime, evaItem.updateTime) &&
                Objects.equals(createTime, evaItem.createTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, evaId, num, require, grade, remark, updateTime, createTime);
        result = 31 * result + Arrays.hashCode(results);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("evaId", evaId)
                .append("num", num)
                .append("require", require)
                .append("grade", grade)
                .append("results", results)
                .append("remark", remark)
                .append("updateTime", updateTime)
                .append("createTime", createTime)
                .toString();
    }
}
