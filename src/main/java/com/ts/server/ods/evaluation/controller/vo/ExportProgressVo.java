package com.ts.server.ods.evaluation.controller.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 导出进度输出对象
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ExportProgressVo {
    @ApiModelProperty("进度")
    private final int pragress;

    public ExportProgressVo(int pragress) {
        this.pragress = pragress;
    }

    public int getPragress() {
        return pragress;
    }
}
