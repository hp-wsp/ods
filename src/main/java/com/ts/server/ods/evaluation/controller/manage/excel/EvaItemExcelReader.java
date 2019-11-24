package com.ts.server.ods.evaluation.controller.manage.excel;

import com.ts.server.ods.common.excel.reader.ExcelReader;
import com.ts.server.ods.common.excel.reader.ReadResult;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.service.EvaItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 读取测评项目Excel
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class EvaItemExcelReader extends ExcelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaItemExcelReader.class);

    private final String evaId;
    private final EvaItemService service;

    /**
     * 构造{@link EvaItemExcelReader}
     *
     * @param evaId 评测编号
     * @param service {@link EvaItemService}
     */
    public EvaItemExcelReader(String evaId, EvaItemService service){
        this.evaId = evaId;
        this.service = service;
    }

    @Override
    protected boolean isHeader(int index, Row row) {
        return index == 0 &&
                StringUtils.equals(StringUtils.remove(getCellContent(row, 1), ' '), "指标");
    }

    private String getCellContent(Row row, int col){
        String s = row.getCell(col).getRichStringCellValue().getString();
        s = StringUtils.replaceChars(s, '\n', ' ');
        s = StringUtils.replaceChars(s, '\t', ' ');
        s = StringUtils.trim(s);
        return s;
    }

    @Override
    protected void read(ReadResult result, int index, Row row) {
        try{
            String num = getCellContent(row, 1);

            if(StringUtils.isBlank(num)){
                LOGGER.warn("Import row num is blank");
                return ;
            }

            result.incCount();
            EvaItem t = new EvaItem();
            t.setEvaId(evaId);
            t.setNum(num);
            t.setRequire(getCellContent(row, 2));
            t.setGrade(getCellContent(row, 3));
            String resultStr = getCellContent(row, 4);
            LOGGER.debug("Import excel index={},result={}", index, resultStr);
            String[] results = Arrays.stream(StringUtils.split(resultStr, " ")).map(StringUtils::trim)
                    .filter(e -> e.length() > 0).toArray(String[]::new);
            t.setResults(results);
            t.setRemark(getCellContent(row, 5));

            service.importItem(t);
        }catch (Exception e){
            LOGGER.error("Import evaluation item fail index={}, row={}", index, row.getRowNum());
            result.addErrorRow(row.getRowNum(), "");
        }
    }
}
