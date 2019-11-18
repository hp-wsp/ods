package com.ts.server.ods;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 腾讯短信配置
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@ConfigurationProperties(prefix = "qq.sms")
public class SmsProperties {
    private int appid;
    private String appKey;
    private String sign;
    private String passwordTmp;
    private String applyTmp;
    private String backTmp;
    private String cancelBackTmp;
    private String urgeTmp;

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPasswordTmp() {
        return passwordTmp;
    }

    public void setPasswordTmp(String passwordTmp) {
        this.passwordTmp = passwordTmp;
    }

    public String getApplyTmp() {
        return applyTmp;
    }

    public void setApplyTmp(String applyTmp) {
        this.applyTmp = applyTmp;
    }

    public String getBackTmp() {
        return backTmp;
    }

    public void setBackTmp(String backTmp) {
        this.backTmp = backTmp;
    }

    public String getCancelBackTmp() {
        return cancelBackTmp;
    }

    public void setCancelBackTmp(String cancelBackTmp) {
        this.cancelBackTmp = cancelBackTmp;
    }

    public String getUrgeTmp() {
        return urgeTmp;
    }

    public void setUrgeTmp(String urgeTmp) {
        this.urgeTmp = urgeTmp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("appid", appid)
                .append("appKey", appKey)
                .append("sign", sign)
                .append("passwordTmp", passwordTmp)
                .append("applyTmp", applyTmp)
                .append("backTmp", backTmp)
                .append("cancelBackTmp", cancelBackTmp)
                .append("urgeTmp", urgeTmp)
                .toString();
    }
}
