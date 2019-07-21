package com.ts.server.ods.evaluation.controller.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 评测汇编输出对象
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaluationItemVo {
    @ApiModelProperty("序号")
    private final int index;
    @ApiModelProperty("指标编号")
    private final String num;
    @ApiModelProperty("具体要求")
    private final String require;
    @ApiModelProperty("评分要求")
    private final String grade;
    private final List<Resource> resources;

    public EvaluationItemVo(int index, String num, String require, String grade, List<Resource> resources) {
        this.index = index;
        this.num = num;
        this.require = require;
        this.grade = grade;
        this.resources = resources;
    }

    public int getIndex() {
        return index;
    }

    public String getNum() {
        return num;
    }

    public String getRequire() {
        return require;
    }

    public String getGrade() {
        return grade;
    }

    public List<Resource> getResources() {
        return resources;
    }


    /**
     * 材料
     */
    public static class Resource {
        private final String id;
        private final String filename;

        public Resource(String id, String filename) {
            this.id = id;
            this.filename = filename;
        }

        public String getId() {
            return id;
        }

        public String getFilename() {
            return filename;
        }
    }
}
