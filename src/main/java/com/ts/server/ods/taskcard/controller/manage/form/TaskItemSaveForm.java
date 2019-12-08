package com.ts.server.ods.taskcard.controller.manage.form;

import com.ts.server.ods.taskcard.domain.TaskCardItem;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新增测评卡指标提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskItemSaveForm {
    @ApiModelProperty(value = "任务编号", required = true)
    @NotBlank
    private String cardId;
    @ApiModelProperty(value = "测评指标系统编号", required = true)
    @NotBlank
    private String evaItemId;
    @ApiModelProperty(value = "具体要求", required = true)
    @NotBlank
    private String requireContent;
    @ApiModelProperty(value = "评分标准", required = true)
    @NotBlank
    private String gradeContent;
    @ApiModelProperty(value = "说明")
    private String remark;
    @ApiModelProperty(value = "赋权", required = true)
    @Min(0)
    private int score;
    @ApiModelProperty("结果集合")
    @NotEmpty
    private List<String> results;

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

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public TaskCardItem toDomain(){
        TaskCardItem t = new TaskCardItem();

        t.setCardId(cardId);
        t.setEvaItemId(evaItemId);
        t.setRequireContent(requireContent);
        t.setGradeContent(gradeContent);
        t.setScore(score);
        t.setRemark(remark);
        t.setResults(results.stream()
                .map(e -> new TaskCardItem.TaskItemResult(e, 0))
                .collect(Collectors.toList()));

        return t;
    }
}
