package com.ts.server.ods.controller.vo;

import com.ts.server.ods.common.excel.reader.ReadResult;

import java.util.List;

/**
 * 导入输出
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ImportVo {
    private final int count;
    private final int errCount;
    private final List<ReadResult.RowError> rowErrors;

    /**
     * 构造{@link ImportVo}
     *
     * @param count 导入行数
     * @param errCount 导入错误行数
     * @param rowErrors 导入错误行数信息
     */
    public ImportVo(int count, int errCount, List<ReadResult.RowError> rowErrors) {
        this.count = count;
        this.errCount = errCount;
        this.rowErrors = rowErrors;
    }

    public int getCount() {
        return count;
    }

    public int getErrCount() {
        return errCount;
    }

    public List<ReadResult.RowError> getRowErrors() {
        return rowErrors;
    }
}
