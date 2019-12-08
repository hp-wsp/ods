package com.ts.server.ods.taskcard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * 任务指标申报材料
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class Declaration {
    @ApiModelProperty("编号")
    private String id;
    @ApiModelProperty("测评卡编号")
    private String cardId;
    @ApiModelProperty("测评卡指标编号")
    private String cardItemId;
    @ApiModelProperty("测评指标编号")
    private String evaItemId;
    @ApiModelProperty("文件名称")
    private String fileName;
    @ApiModelProperty("文件大小")
    private int fileSize;
    @ApiModelProperty("文件类型")
    private String contentType;
    @ApiModelProperty("保存路径")
    @JsonIgnore
    private String path;
    @ApiModelProperty("申报用户名")
    private String decUsername;
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

    public String getCardItemId() {
        return cardItemId;
    }

    public void setCardItemId(String cardItemId) {
        this.cardItemId = cardItemId;
    }

    public String getEvaItemId() {
        return evaItemId;
    }

    public void setEvaItemId(String evaItemId) {
        this.evaItemId = evaItemId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDecUsername() {
        return decUsername;
    }

    public void setDecUsername(String decUsername) {
        this.decUsername = decUsername;
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
        Declaration that = (Declaration) o;
        return fileSize == that.fileSize &&
                Objects.equals(id, that.id) &&
                Objects.equals(cardId, that.cardId) &&
                Objects.equals(cardItemId, that.cardItemId) &&
                Objects.equals(evaItemId, that.evaItemId) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(path, that.path) &&
                Objects.equals(decUsername, that.decUsername) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardId, cardItemId, evaItemId, fileName, fileSize, contentType, path, decUsername, createTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("cardId", cardId)
                .append("cardItemId", cardItemId)
                .append("evaItemId", evaItemId)
                .append("fileName", fileName)
                .append("fileSize", fileSize)
                .append("contentType", contentType)
                .append("path", path)
                .append("decUsername", decUsername)
                .append("createTime", createTime)
                .toString();
    }
}
