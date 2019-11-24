package com.ts.server.ods.evaluation.controller.manage.excel;

import com.ts.server.ods.common.excel.writer.ExcelWriter;
import com.ts.server.ods.common.excel.writer.HttpExcelWriter;
import com.ts.server.ods.evaluation.controller.manage.vo.EvaluationItemVo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 材料汇编写Excel
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaCollectExcelWriter extends HttpExcelWriter<EvaluationItemVo> {
    private static final String[] HEADS = new String[]{"序号", "指标", "具体要求", "评分标准", "材料"};

    /**
     * 构造{@link EvaCollectExcelWriter}
     *
     * @param response {@link HttpServletResponse}
     * @param is2003 true:是2003格式excel
     * @param filename 导出文件名
     * @throws IOException
     */
    public EvaCollectExcelWriter(HttpServletResponse response, boolean is2003, String filename)throws IOException {
        super(response, is2003, filename);
    }

    @Override
    protected void writeRow(Row r, EvaluationItemVo t) {
        Cell cell0 = r.createCell(0);
        cell0.setCellValue(t.getIndex() + 1);
        Cell cell1 = r.createCell(1);
        cell1.setCellValue(t.getNum());
        Cell cell2 = r.createCell(2);
        cell2.setCellValue(t.getRequire());
        Cell cell3 = r.createCell(3);
        cell3.setCellValue(t.getGrade());
        String fileNames = buildFilenames(t);
        Cell cell4 = r.createCell(4);
        cell4.setCellValue(fileNames);
    }

    private String buildFilenames(EvaluationItemVo t){
        return  t.getResources().stream()
                .map(EvaluationItemVo.Resource::getFilename)
                .collect(Collectors.joining("\n"));
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
