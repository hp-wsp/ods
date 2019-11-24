package com.ts.server.ods.common.excel.reader;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

/**
 * 读取Excel
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public abstract class ExcelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReader.class);

    /**
     * 读取Excel
     *
     * @param inputStream {@link InputStream}
     * @return {@link ReadResult}
     * @throws IOException
     */
    public ReadResult read(InputStream inputStream)throws IOException{
        final ReadResult result = new ReadResult();

        try(Workbook workbook = createWorkbook(inputStream)){
            Sheet sheet = workbook.getSheetAt(0);

            int index = 0;
            for(Row row : sheet){
                if(isHeader(index++, row)){
                    LOGGER.debug("Read excel is head index={}, rowNum={}", index, row.getRowNum());
                    continue;
                }
                read(result, index, row);
            }
        }

        return result;
    }

    private Workbook createWorkbook(InputStream inputStream)throws IOException{
        return WorkbookFactory.create(inputStream);
    }

    /**
     * 行是头
     *
     * @param index 行数
     * @param row {@link Row}
     * @return true:表头
     */
    protected abstract boolean isHeader(int index, Row row);

    /**
     * 读取表格
     *
     * @param result {@link ReadResult}
     * @param index 行号
     * @param row {@link Row}
     */
    protected abstract void read(ReadResult result, int index, Row row);
}
