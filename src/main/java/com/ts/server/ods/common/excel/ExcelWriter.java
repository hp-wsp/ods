package com.ts.server.ods.common.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
/**
 * 写Excel方法
 *
 * <a href="mailto:hhywangwei@mgail.com">WangWei</a>
 */
public class ExcelWriter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriter.class);

    private final BiConsumer<Row, T> rowConsumer;
    private final boolean is2003;
    private final List<String> headData;
    private final boolean isHeader;

    public ExcelWriter(BiConsumer<Row, T> rowConsumer, boolean is2003){
        this(rowConsumer, is2003, Collections.emptyList());
    }

    public ExcelWriter(BiConsumer<Row, T> rowConsumer, boolean is2003, List<String> headData){
        this.rowConsumer = rowConsumer;
        this.is2003 = is2003;
        this.headData= headData;
        this.isHeader = !headData.isEmpty();
    }

    public void write(OutputStream outputStream, int offset, List<T> data)throws IOException{
        Workbook workbook = buildWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        if(isWriteHead(offset)){
            writeHead(sheet, headData);
        }

        int startRow = isHeader? offset + 1: offset;
        for(int i = 0; i < data.size(); i++){
            Row row = sheet.createRow(startRow +i);
            rowConsumer.accept(row, data.get(i));
        }
        workbook.write(outputStream);
        workbook.close();
    }

    private boolean  isWriteHead(int offset){
        return isHeader && offset == 0;
    }

    private void writeHead(Sheet sheet, List<String> data){
        Row row = sheet.createRow(0);
        for(int i = 0; i < data.size(); i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(data.get(i));
        }
    }

    private Workbook buildWorkbook(){
        return is2003? new HSSFWorkbook() : new XSSFWorkbook();
    }
}
