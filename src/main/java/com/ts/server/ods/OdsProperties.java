package com.ts.server.ods;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ods")
public class OdsProperties {

    /**
     * 上传资源保存路径
     */
    private String resource;

    /**
     * 生成view目录
     */
    private String viewDir;

    /**
     * 生成view url
     */
    private String viewUrl;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getViewDir() {
        return viewDir;
    }

    public void setViewDir(String viewDir) {
        this.viewDir = viewDir;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("resource", resource)
                .append("viewDir", viewDir)
                .append("viewUrl", viewUrl)
                .toString();
    }
}
