package com.ts.server.ods.etask.controller;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.base.service.GradeRateService;
import com.ts.server.ods.common.excel.ExcelReader;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.controller.form.TaskItemSaveForm;
import com.ts.server.ods.etask.controller.form.TaskItemUpdateForm;
import com.ts.server.ods.etask.controller.vo.TaskItemVo;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.etask.service.TaskItemService;
import com.ts.server.ods.logger.service.OptLogService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 评测卡指标API接口
 *
 * @author  <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/task/item")
@Api(value = "/manage/task/item", tags = "评测卡指标API接口")
public class TaskItemController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskItemController.class);

    private final TaskCardService cardService;
    private final TaskItemService service;
    private final GradeRateService rateService;
    private final OptLogService optLogService;

    @Autowired
    public TaskItemController(TaskItemService service, TaskCardService cardService, GradeRateService rateService,
                              OptLogService optLogService) {
        this.service = service;
        this.cardService = cardService;
        this.rateService = rateService;
        this.optLogService = optLogService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新增评测卡指标")
    public ResultVo<TaskItemVo> save(@Valid @RequestBody TaskItemSaveForm form){
        TaskItem t = service.save(form.toDomain());

        optLogService.save("新增评测卡指标", new String[]{"编号", "评测指标编号"},
                new String[]{t.getId(), t.getEvaNum()}, getCredential().getUsername());

        return ResultVo.success(new TaskItemVo(t));
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改评测卡指标")
    public ResultVo<TaskItemVo> update(@Valid @RequestBody TaskItemUpdateForm form){
        TaskItem t = service.update(form.toDomain());

        optLogService.save("修改评测卡指标", new String[]{"编号", "评测指标编号"},
                new String[]{t.getId(), t.getEvaNum()}, getCredential().getUsername());

        return ResultVo.success(new TaskItemVo(t));
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("删除评测卡指标")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        TaskItem t = service.get(id);

        boolean ok = service.delete(id);
        if(ok){
            optLogService.save("删除评测卡指标", new String[]{"编号", "评测指标编号"},
                    new String[]{t.getId(), t.getEvaNum()}, getCredential().getUsername());
        }

        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到评测卡指标")
    public ResultVo<TaskItemVo> get(@PathVariable("id")String id){
        return ResultVo.success(new TaskItemVo(service.get(id)));
    }

    @PostMapping(value = "import", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("导入评测卡指标")
    public ResultVo<OkVo> importItem(@RequestParam(value = "file") @ApiParam(value = "上传文件", required = true) MultipartFile file,
                                     @RequestParam(value = "cardId") @ApiParam(value = "评测卡编号", required = true) String cardId){

        ExcelReader reader = buildImportExcelReader(cardId);

        try(InputStream inputStream= file.getInputStream()){
            reader.read(inputStream);
            cardService.updateItemCount(cardId);
            cardService.updateScore(cardId);

            optLogService.save("导入测评任务指标", new String[]{"测评任务编号"},
                    new String[]{cardId}, getCredential().getUsername());

            return ResultVo.success(new OkVo(true));
        }catch (IOException e){
            LOGGER.error("Import item fail throw={}", e.getMessage());
            return ResultVo.success(new OkVo(false));
        }
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }

    private ExcelReader buildImportExcelReader(String cardId){
        Map<String, Integer> gradeRates = gradeRates();
        TaskCard card = cardService.get(cardId);
        return new ExcelReader((i, r) -> {
            boolean isHeard = StringUtils.equals(StringUtils.remove(getCellContent(r, 1), ' '), "指标");

            if(isHeard){
                return ;
            }

            TaskItem t = new TaskItem();
            t.setCardId(card.getId());
            t.setEvaNum(getCellContent(r, 1));
            t.setRequireContent(getCellContent(r, 2));
            t.setScore((int)r.getCell(3).getNumericCellValue());
            t.setGradeContent(getCellContent(r, 4));
            String resultStr = getCellContent(r, 5);
            LOGGER.debug("Import excel index={},result={}", i, resultStr);
            t.setResults(buildResults(resultStr, t.getScore(), gradeRates));
            t.setRemark(getCellContent(r, 6));

            service.importItem(card, t);
        });
    }

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

    private List<TaskItem.TaskItemResult> buildResults(String resultStr, int score, Map<String, Integer> gradeRates){
        return Arrays.stream(StringUtils.split(resultStr, " "))
                .map(StringUtils::trim)
                .filter(e -> e.length() > 0)
                .map(e -> new TaskItem.TaskItemResult(e, (score * gradeRates.getOrDefault(e, 0))/100))
                .collect(Collectors.toList());
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询评测卡指标")
    public ResultPageVo<TaskItemVo> query(
            @ApiParam(value = "任务编号") @RequestParam(required = false) String taskId,
            @ApiParam(value = "指标编号") @RequestParam(required = false) String num,
            @ApiParam(value = "具体要求") @RequestParam(required = false) String require,
            @ApiParam(value = "评分标准") @RequestParam(required = false) String grade,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        List<TaskItemVo> data = service.query( taskId, num, require, grade,page * rows, rows).stream()
                .map(TaskItemVo::new).collect(Collectors.toList());
        return new ResultPageVo.Builder<>(page, rows, data)
                .count(isCount, () -> service.count( taskId, num, require, grade))
                .build();
    }
}
