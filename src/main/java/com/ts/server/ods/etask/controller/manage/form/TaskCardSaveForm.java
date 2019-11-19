package com.ts.server.ods.etask.controller.manage.form;

import com.ts.server.ods.etask.domain.TaskCard;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 *  新增测评卡提交数据
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskCardSaveForm {
    @ApiModelProperty(value = "测评编号", required =  true)
    @NotBlank
    private String evaId;
    @ApiModelProperty(value = "公司编号", required = true)
    @NotBlank
    private String companyId;
    @ApiModelProperty(value = "审核员编号", required = true)
    @NotBlank
    private String assId;

    public String getEvaId() {
        return evaId;
    }

    public void setEvaId(String evaId) {
        this.evaId = evaId;
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

    public TaskCard toDomain(){
        TaskCard t = new TaskCard();

        t.setEvaId(evaId);
        t.setCompanyId(companyId);
        t.setAssId(assId);

        return t;
    }
}
