package com.ts.server.ods.etask.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * 评测卡
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskCard {
    @ApiModelProperty(value = "单位编号")
    private String id;
    @ApiModelProperty(value = "测评编号")
    private String evaId;
    @ApiModelProperty(value = "测评名称")
    private String evaName;
    @ApiModelProperty(value = "公司编号")
    private String companyId;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "单位分组")
    private String companyGroup;
    @ApiModelProperty(value = "审核员编号")
    private String assId;
    @ApiModelProperty(value = "审核员用户名")
    private String assUsername;
    @ApiModelProperty(value = "审核员姓名")
    private String assName;
    @ApiModelProperty(value = "申报员编号")
    private String decId;
    @ApiModelProperty(value = "申报员用户名")
    private String decUsername;
    @ApiModelProperty(value = "申报员姓名")
    private String decName;
    @ApiModelProperty(value = "任务是否开启")
    private boolean open;
    @ApiModelProperty(value = "任务卡状态")
    private Status status;
    @ApiModelProperty(value = "赋权")
    private int score;
    @ApiModelProperty(value = "得分;status==GRADE才有效")
    private int gradeScore;
    @ApiModelProperty(value = "总测评数")
    private int itemCount;
    @ApiModelProperty(value = "申报数")
    private int decCount;
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public enum Status {
        WAIT, SUBMIT, BACK, GRADE
    }

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

    public String getEvaName() {
        return evaName;
    }

    public void setEvaName(String evaName) {
        this.evaName = evaName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyGroup() {
        return companyGroup;
    }

    public void setCompanyGroup(String companyGroup) {
        this.companyGroup = companyGroup;
    }

    public String getAssId() {
        return assId;
    }

    public void setAssId(String assId) {
        this.assId = assId;
    }

    public String getAssUsername() {
        return assUsername;
    }

    public void setAssUsername(String assUsername) {
        this.assUsername = assUsername;
    }

    public String getAssName() {
        return assName;
    }

    public void setAssName(String assName) {
        this.assName = assName;
    }

    public String getDecId() {
        return decId;
    }

    public void setDecId(String decId) {
        this.decId = decId;
    }

    public String getDecUsername() {
        return decUsername;
    }

    public void setDecUsername(String decUsername) {
        this.decUsername = decUsername;
    }

    public String getDecName() {
        return decName;
    }

    public void setDecName(String decName) {
        this.decName = decName;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getGradeScore() {
        return gradeScore;
    }

    public void setGradeScore(int gradeScore) {
        this.gradeScore = gradeScore;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getDecCount() {
        return decCount;
    }

    public void setDecCount(int decCount) {
        this.decCount = decCount;
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
        TaskCard taskCard = (TaskCard) o;
        return open == taskCard.open &&
                score == taskCard.score &&
                gradeScore == taskCard.gradeScore &&
                itemCount == taskCard.itemCount &&
                decCount == taskCard.decCount &&
                Objects.equals(id, taskCard.id) &&
                Objects.equals(evaId, taskCard.evaId) &&
                Objects.equals(evaName, taskCard.evaName) &&
                Objects.equals(companyId, taskCard.companyId) &&
                Objects.equals(companyName, taskCard.companyName) &&
                Objects.equals(companyGroup, taskCard.companyGroup) &&
                Objects.equals(assId, taskCard.assId) &&
                Objects.equals(assUsername, taskCard.assUsername) &&
                Objects.equals(assName, taskCard.assName) &&
                Objects.equals(decId, taskCard.decId) &&
                Objects.equals(decUsername, taskCard.decUsername) &&
                Objects.equals(decName, taskCard.decName) &&
                status == taskCard.status &&
                Objects.equals(updateTime, taskCard.updateTime) &&
                Objects.equals(createTime, taskCard.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, evaId, evaName, companyId, companyName, companyGroup, assId, assUsername, assName, decId, decUsername, decName, open, status, score, gradeScore, itemCount, decCount, updateTime, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("evaId", evaId)
                .append("evaName", evaName)
                .append("companyId", companyId)
                .append("companyName", companyName)
                .append("companyGroup", companyGroup)
                .append("assId", assId)
                .append("assUsername", assUsername)
                .append("assName", assName)
                .append("decId", decId)
                .append("decUsername", decUsername)
                .append("decName", decName)
                .append("open", open)
                .append("status", status)
                .append("score", score)
                .append("gradeScore", gradeScore)
                .append("itemCount", itemCount)
                .append("decCount", decCount)
                .append("updateTime", updateTime)
                .append("createTime", createTime)
                .toString();
    }
}
