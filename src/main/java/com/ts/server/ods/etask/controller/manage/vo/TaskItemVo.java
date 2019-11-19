package com.ts.server.ods.etask.controller.manage.vo;

import com.ts.server.ods.etask.domain.TaskItem;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务卡指标输出
 *
 * @author <a href="mailtp:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskItemVo {
    @ApiModelProperty(value = "编号")
    private final String id;
    @ApiModelProperty(value = "测评卡编号")
    private final String cardId;
    @ApiModelProperty(value = "测评指标系统编号")
    private final String evaItemId;
    @ApiModelProperty("测评指标编号")
    private final String evaNum;
    @ApiModelProperty("具体要求")
    private final String requireContent;
    @ApiModelProperty("评分标准")
    private final String gradeContent;
    @ApiModelProperty("说明")
    private final String remark;
    @ApiModelProperty("赋权")
    private final int score;
    @ApiModelProperty("结果集合")
    private final List<String> results;
    @ApiModelProperty("是否评分")
    private final boolean grade;
    @ApiModelProperty("分等级")
    private final String gradeLevel;
    @ApiModelProperty("得分")
    private final int gradeScore;
    @ApiModelProperty("申报材料问题")
    private final String gradeRemark;
    @ApiModelProperty("修改时间")
    private final Date updateTime;
    @ApiModelProperty("创建时间")
    private final Date createTime;

    public TaskItemVo(TaskItem taskItem){
        this.id = taskItem.getId();
        this.cardId = taskItem.getCardId();
        this.evaItemId = taskItem.getEvaItemId();
        this.evaNum = taskItem.getEvaNum();
        this.requireContent = taskItem.getRequireContent();
        this.gradeContent = taskItem.getGradeContent();
        this.remark = taskItem.getRemark();
        this.score = taskItem.getScore();
        this.results = taskItem.getResults().stream().map(TaskItem.TaskItemResult::getLevel).collect(Collectors.toList());
        this.grade = taskItem.isGrade();
        this.gradeLevel = taskItem.getGradeLevel();
        this.gradeScore = taskItem.getGradeScore();
        this.gradeRemark = taskItem.getGradeRemark();
        this.updateTime = taskItem.getUpdateTime();
        this.createTime = taskItem.getCreateTime();
    }

    public String getId() {
        return id;
    }

    public String getCardId() {
        return cardId;
    }

    public String getEvaItemId() {
        return evaItemId;
    }

    public String getEvaNum() {
        return evaNum;
    }

    public String getRequireContent() {
        return requireContent;
    }

    public String getGradeContent() {
        return gradeContent;
    }

    public String getRemark() {
        return remark;
    }

    public int getScore() {
        return score;
    }

    public List<String> getResults() {
        return results;
    }

    public boolean isGrade() {
        return grade;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public int getGradeScore() {
        return gradeScore;
    }

    public String getGradeRemark() {
        return gradeRemark;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
