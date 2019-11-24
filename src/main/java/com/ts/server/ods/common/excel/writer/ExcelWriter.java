package com.ts.server.ods.common.excel.writer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 写Excel
 *
 * <a href="mailto:hhywangwei@mgail.com">WangWei</a>
 *
 * @param T 导出数据对象类型
 */
public abstract class ExcelWriter<T> implements Closeable {
    private final AtomicInteger rowNum = new AtomicInteger(0);
    private final OutputStream outputStream;
    private final Workbook workbook;
    private final Sheet sheet;

    /**
     * 构造{@link ExcelWriter}
     *
     * @param outputStream {@link OutputStream}
     * @param is2003 true:2003格式
     */
    public ExcelWriter(OutputStream outputStream, boolean is2003){
        this.outputStream = outputStream;
        this.workbook = buildWorkbook(is2003);
        this.sheet = workbook.createSheet("sheet1");
    }

    private Workbook buildWorkbook(boolean is2003){
        return is2003? new HSSFWorkbook() : new XSSFWorkbook();
    }

    public void write(int offset, List<T> data){

        if(offset == 0){
            int hRow = writeHeader(workbook, sheet);
            rowNum.addAndGet(hRow);
        }

        for(T t: data){
            Row row = sheet.createRow(rowNum.incrementAndGet());
            writeRow(row, t);
        }
    }

    /**
     * 写Excel头
     *
     * @param Workbook {@link Workbook}
     * @param sheet {@link Sheet}
     * @return 表格头行数
     */
    protected int writeHeader(Workbook workbook, Sheet sheet){
        return 0;
    }

    /**
     * 写Excel footer
     *
     * @param workbook {@link Workbook}
     * @param sheet {@link Sheet}
     */
    protected void writeFooter(Workbook workbook, Sheet sheet){
        //子类实现
    }

    /**
     * 写行
     *
     * @param row {@link Row}
     * @param t T
     */
    protected abstract void  writeRow(Row row, T t);

    @Override
    public void close() throws IOException {
        try{
            workbook.write(outputStream);
        }finally {
            outputStream.close();
        }
    }
}
