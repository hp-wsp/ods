package com.ts.server.ods.common.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * 读取Excel
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ExcelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReader.class);

    private final BiConsumer<Integer, Row>  rowConsumer;

    public ExcelReader(BiConsumer<Integer, Row>  rowConsumer) {
        this.rowConsumer = rowConsumer;
    }

    public void read(InputStream inputStream)throws IOException{
        Workbook workbook = createWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int index = 0;
        for(Row row : sheet){
            rowConsumer.accept(index, row);
            index++;
        }
        workbook.close();
    }

    private Workbook createWorkbook(InputStream inputStream)throws IOException{
        return WorkbookFactory.create(inputStream);
    }
}
