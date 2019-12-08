package com.ts.server.ods;

import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.base.service.GradeRateService;
import com.ts.server.ods.taskcard.domain.TaskCardItem;
import com.ts.server.ods.taskcard.service.TaskCardService;
import com.ts.server.ods.taskcard.service.TaskItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class UpdateTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTest.class);

    @Autowired
    private TaskItemService service;
    @Autowired
    private TaskCardService cardService;
    @Autowired
    private GradeRateService rateService;

    //@Test
    public void testImport(){
//        ExcelReader reader = buildImportExcelReader("7960feecf70b4dd3b09bd3f272e6cd74");
//
//        try(InputStream inputStream= UpdateTest.class.getResourceAsStream("import_task.xlsx")){
//            reader.read(inputStream);
//
//        }catch (IOException e){
//            LOGGER.error("Import item fail throw={}", e.getMessage());
//        }
    }

//    private ExcelReader buildImportExcelReader(String cardId){
////        Map<String, Integer> gradeRates = gradeRates();
////        TaskCard card = cardService.get(cardId);
////        return new ExcelReader((i, r) -> {
////            boolean isHeard = StringUtils.equals(StringUtils.remove(getCellContent(r, 1), ' '), "指标");
////
////            if(isHeard){
////                return ;
////            }
////
////            TaskCardItem t = new TaskCardItem();
////            t.setCardId(card.getId());
////            t.setEvaNum(getCellContent(r, 1));
////            t.setRequireContent(getCellContent(r, 2));
////            t.setScore((int)r.getCell(3).getNumericCellValue());
////            t.setGradeContent(getCellContent(r, 4));
////            String resultStr = getCellContent(r, 5);
////            LOGGER.debug("Import excel index={},result={}", i, resultStr);
////            t.setResults(buildResults(resultStr, t.getScore(), gradeRates));
////            t.setRemark(getCellContent(r, 6));
////
////            service.importItem(card, t);
////        });
//    }

    private Map<String, Integer> gradeRates(){
        return rateService.queryAll().stream().collect(Collectors.groupingBy(GradeRate::getLevel))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0).getRate()));
    }

    private String getCellContent(Row row, int col){
        String s = row.getCell(col).getRichStringCellValue().getString();
        s = StringUtils.replaceChars(s, '\n', ' ');
        s = StringUtils.replaceChars(s, '\t', ' ');
        s = StringUtils.trim(s);
        return s;
    }

    private List<TaskCardItem.TaskItemResult> buildResults(String resultStr, int score, Map<String, Integer> gradeRates){
        return Arrays.stream(StringUtils.split(resultStr, " "))
                .map(StringUtils::trim)
                .filter(e -> e.length() > 0)
                .map(e -> new TaskCardItem.TaskItemResult(e, (score * gradeRates.getOrDefault(e, 0))/100))
                .collect(Collectors.toList());
    }

}
