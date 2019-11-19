package com.ts.server.ods.etask.controller.manage.form;

import com.ts.server.ods.etask.domain.TaskCard;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 修改评测卡提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskCardUpdateForm {
    @ApiModelProperty(value = "编号", required = true)
    @NotBlank
    private String id;
    @ApiModelProperty(value = "公司编号", required = true)
    @NotBlank
    private String companyId;
    @ApiModelProperty(value = "审核员编号", required = true)
    @NotBlank
    private String assId;
    @ApiModelProperty(value = "申报员编号", required = true)
    @NotBlank
    private String decId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getAssId() {
        return assId;
    }

    public void setAssId(String assId) {
        this.assId = assId;
    }

    public String getDecId() {
        return decId;
    }

    public void setDecId(String decId) {
        this.decId = decId;
    }

    public TaskCard toDomain(){
        TaskCard t = new TaskCard();

        t.setId(id);
        t.setCompanyId(companyId);
        t.setAssId(assId);
        t.setDecId(decId);

        return t;
    }
}
