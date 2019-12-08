package com.ts.server.ods.evaluation.controller.manage.excel;

import com.ts.server.ods.common.excel.writer.HttpExcelWriter;
import com.ts.server.ods.taskcard.domain.TaskCard;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 测评分数写入Excel
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaScoreExcelWriter extends HttpExcelWriter<TaskCard> {
    private static final String[] HEADS = new String[]{"序号", "单位", "分数"};

    /**
     * 构造{@link EvaScoreExcelWriter}
     *
     * @param response {@link HttpServletResponse}
     * @param is2003 true:2003格式excel
     * @param filename 导出文件名
     * @throws IOException
     */
    public EvaScoreExcelWriter(HttpServletResponse response, boolean is2003, String filename) throws IOException {
        super(response, is2003, filename);
    }

    @Override
    protected void writeRow(Row r, TaskCard t) {
        Cell cell0 = r.createCell(0);
        cell0.setCellValue(r.getRowNum());
        Cell cell1 = r.createCell(1);
        cell1.setCellValue(t.getCompanyName());
        Cell cell2 = r.createCell(2);
        cell2.setCellValue(formatScore(t.getGradeScore()));
    }

    private String formatScore(int score){
        int remain = score % 100;
        return  remain == 0? String.valueOf(score / 100): String.format("%d.%02d", score/ 100, remain);
    }

    @Override
    protected int writeHeader(Workbook workbook, Sheet sheet) {
        Row row = sheet.createRow(0);
        for(int i = 0; i < HEADS.length; i++){
            Cell cell = row.getCell(i);
            cell.setCellValue(HEADS[i]);
        }
        return 1;
    }
}
