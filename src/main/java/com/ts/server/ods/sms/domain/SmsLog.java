package com.ts.server.ods.sms.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * 短信日志
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class SmsLog {
    @ApiModelProperty("编号")
    private String id;
    @ApiModelProperty("接收短信手机号")
    private String phone;
    @ApiModelProperty("发送短信息内容")
    private String content;
    @ApiModelProperty("单位名称")
    private String companyName;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("错误码")
    private int errCode;
    @ApiModelProperty("错误信息")
    private String errMsg;
    @ApiModelProperty("创建时间")
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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
        SmsLog smsLog = (SmsLog) o;
        return errCode == smsLog.errCode &&
                Objects.equals(id, smsLog.id) &&
                Objects.equals(phone, smsLog.phone) &&
                Objects.equals(content, smsLog.content) &&
                Objects.equals(companyName, smsLog.companyName) &&
                Objects.equals(name, smsLog.name) &&
                Objects.equals(errMsg, smsLog.errMsg) &&
                Objects.equals(createTime, smsLog.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, content, companyName, name, errCode, errMsg, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("phone", phone)
                .append("content", content)
                .append("companyName", companyName)
                .append("name", name)
                .append("errCode", errCode)
                .append("errMsg", errMsg)
                .append("createTime", createTime)
                .toString();
    }
}
