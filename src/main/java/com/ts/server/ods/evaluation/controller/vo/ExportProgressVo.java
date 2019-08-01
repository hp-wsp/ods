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
    @ApiModelProperty("导出编号")
    private final String exportId;

    public ExportProgressVo(int pragress){
        this(pragress, null );
    }

    public ExportProgressVo(int pragress, String exportId) {
        this.pragress = pragress;
        this.exportId = exportId;
    }

    public int getPragress() {
        return pragress;
    }

    public String getExportId() {
        return exportId;
    }
}
