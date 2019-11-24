package com.ts.server.ods.etask.controller.manage.excel;

import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.base.service.GradeRateService;
import com.ts.server.ods.common.excel.reader.ExcelReader;
import com.ts.server.ods.common.excel.reader.ReadResult;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.etask.service.TaskItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 读取测评卡项目Excel
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class TaskItemExcelReader extends ExcelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskItemExcelReader.class);

    private final TaskCard card;
    private final TaskItemService service;
    private final  Map<String, Integer> gradeRates;

    /**
     * 构造{@link TaskItemExcelReader}
     *
     * @param card {@link TaskCard}
     * @param service {@link TaskItemService}
     * @param rateService {@link GradeRateService}
     */
    public TaskItemExcelReader(TaskCard card, TaskItemService service, GradeRateService rateService) {
        this.card = card;
        this.service = service;
        this.gradeRates = buildGradeRates(rateService);
    }

    private Map<String, Integer> buildGradeRates(GradeRateService service){
        return service.queryAll().stream().collect(Collectors.groupingBy(GradeRate::getLevel))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0).getRate()));
    }

    @Override
    protected boolean isHeader(int index, Row row) {
        return index == 0 &&
                StringUtils.equals(StringUtils.remove(getCellContent(row, 1), ' '), "指标");
    }

    private String getCellContent(Row row, int col){
        Cell cell = row.getCell(col);
        if(cell == null){
            return "";
        }
        RichTextString text = cell.getRichStringCellValue();
        if(text == null || StringUtils.isBlank(text.getString())){
            return "";
        }
        String s = text.getString();
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
                return ;
            }
            result.incCount();
            TaskItem t = new TaskItem();
            t.setCardId(card.getId());
            t.setEvaNum(num);
            t.setRequireContent(getCellContent(row, 2));
            int score= (int)Math.round(row.getCell(3).getNumericCellValue() * 100);
            t.setScore(score);
            t.setGradeContent(getCellContent(row, 4));
            String resultStr = getCellContent(row, 5);
            t.setResults(buildResults(resultStr, t.getScore(), gradeRates));
            t.setRemark(getCellContent(row, 6));
            t.setShowOrder(index);

            service.importItem(card, t);
        }catch (Exception e){
            LOGGER.error("Import card item fail index={}, row={}, throw={}", index, row.getRowNum(), e.getMessage());
            result.addErrorRow(row.getRowNum(), "");
        }
    }

    private List<TaskItem.TaskItemResult> buildResults(String resultStr, int score, Map<String, Integer> gradeRates){
        return Arrays.stream(StringUtils.split(resultStr, " "))
                .map(StringUtils::trim)
                .filter(e -> e.length() > 0)
                .map(e -> new TaskItem.TaskItemResult(e, (score * gradeRates.getOrDefault(e, 0))/100))
                .collect(Collectors.toList());
    }

}
