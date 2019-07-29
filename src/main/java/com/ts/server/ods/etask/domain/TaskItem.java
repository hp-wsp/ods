package com.ts.server.ods.etask.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 任务卡指标
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskItem {
    @ApiModelProperty(value = "编号")
    private String id;
    @ApiModelProperty(value = "测评卡编号")
    private String cardId;
    @ApiModelProperty(value = "测评指标系统编号")
    private String evaItemId;
    @ApiModelProperty("测评指标编号")
    private String evaNum;
    @ApiModelProperty("具体要求")
    private String requireContent;
    @ApiModelProperty("评分标准")
    private String gradeContent;
    @ApiModelProperty("说明")
    private String remark;
    @ApiModelProperty("赋权")
    private int score;
    @ApiModelProperty("结果集合")
    private List<TaskItemResult> results;
    @ApiModelProperty("是否申报材料")
    private boolean declare;
    @ApiModelProperty("是否评分")
    private boolean grade;
    @ApiModelProperty("分等级")
    private String gradeLevel;
    @ApiModelProperty("得分")
    private int gradeScore;
    @ApiModelProperty("申报材料问题")
    private String gradeRemark;
    @ApiModelProperty("排序")
    private int showOrder = 1000;
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

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getEvaItemId() {
        return evaItemId;
    }

    public void setEvaItemId(String evaItemId) {
        this.evaItemId = evaItemId;
    }

    public String getEvaNum() {
        return evaNum;
    }

    public void setEvaNum(String evaNum) {
        this.evaNum = evaNum;
    }

    public String getRequireContent() {
        return requireContent;
    }

    public void setRequireContent(String requireContent) {
        this.requireContent = requireContent;
    }

    public String getGradeContent() {
        return gradeContent;
    }

    public void setGradeContent(String gradeContent) {
        this.gradeContent = gradeContent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<TaskItemResult> getResults() {
        return results;
    }

    public void setResults(List<TaskItemResult> results) {
        this.results = results;
    }

    public boolean isDeclare() {
        return declare;
    }

    public void setDeclare(boolean declare) {
        this.declare = declare;
    }

    public boolean isGrade() {
        return grade;
    }

    public void setGrade(boolean grade) {
        this.grade = grade;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public int getGradeScore() {
        return gradeScore;
    }

    public void setGradeScore(int gradeScore) {
        this.gradeScore = gradeScore;
    }

    public String getGradeRemark() {
        return gradeRemark;
    }

    public void setGradeRemark(String gradeRemark) {
        this.gradeRemark = gradeRemark;
    }

    public int getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(int showOrder) {
        this.showOrder = showOrder;
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
        TaskItem item = (TaskItem) o;
        return score == item.score &&
                declare == item.declare &&
                grade == item.grade &&
                gradeScore == item.gradeScore &&
                showOrder == item.showOrder &&
                Objects.equals(id, item.id) &&
                Objects.equals(cardId, item.cardId) &&
                Objects.equals(evaItemId, item.evaItemId) &&
                Objects.equals(evaNum, item.evaNum) &&
                Objects.equals(requireContent, item.requireContent) &&
                Objects.equals(gradeContent, item.gradeContent) &&
                Objects.equals(remark, item.remark) &&
                Objects.equals(results, item.results) &&
                Objects.equals(gradeLevel, item.gradeLevel) &&
                Objects.equals(gradeRemark, item.gradeRemark) &&
                Objects.equals(updateTime, item.updateTime) &&
                Objects.equals(createTime, item.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardId, evaItemId, evaNum, requireContent, gradeContent, remark, score, results, declare, grade, gradeLevel, gradeScore, gradeRemark, showOrder, updateTime, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("cardId", cardId)
                .append("evaItemId", evaItemId)
                .append("evaNum", evaNum)
                .append("requireContent", requireContent)
                .append("gradeContent", gradeContent)
                .append("remark", remark)
                .append("score", score)
                .append("results", results)
                .append("declare", declare)
                .append("grade", grade)
                .append("gradeLevel", gradeLevel)
                .append("gradeScore", gradeScore)
                .append("gradeRemark", gradeRemark)
                .append("showOrder", showOrder)
                .append("updateTime", updateTime)
                .append("createTime", createTime)
                .toString();
    }

    public static class TaskItemResult {
        @ApiModelProperty(value = "级别")
        private String level;
        @ApiModelProperty(value = "分值")
        private int score;

        public TaskItemResult(){
        }

        public TaskItemResult(String level, int score) {
            this.level = level;
            this.score = score;
        }
        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TaskItemResult that = (TaskItemResult) o;
            return score == that.score &&
                    Objects.equals(level, that.level);
        }

        @Override
        public int hashCode() {
            return Objects.hash(level, score);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("level", level)
                    .append("score", score)
                    .toString();
        }
    }

}
