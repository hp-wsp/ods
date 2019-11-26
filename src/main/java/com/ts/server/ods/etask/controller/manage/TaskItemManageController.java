package com.ts.server.ods.etask.controller.manage;

import com.ts.server.ods.base.service.GradeRateService;
import com.ts.server.ods.common.excel.reader.ExcelReader;
import com.ts.server.ods.common.excel.reader.ReadResult;
import com.ts.server.ods.controller.vo.ImportVo;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.controller.manage.excel.TaskItemExcelReader;
import com.ts.server.ods.etask.controller.manage.form.TaskItemSaveForm;
import com.ts.server.ods.etask.controller.manage.form.TaskItemUpdateForm;
import com.ts.server.ods.etask.controller.manage.logger.TaskItemLogDetailBuilder;
import com.ts.server.ods.etask.controller.manage.vo.TaskItemVo;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.etask.service.TaskItemService;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 评测卡指标API接口
 *
 * @author  <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/cardItem")
@ApiACL({"ROLE_SYS"})
@Api(value = "/manage/cardItem", tags = "评测卡指标API接口")
public class TaskItemManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskItemManageController.class);

    private final TaskCardService cardService;
    private final TaskItemService service;
    private final GradeRateService rateService;

    @Autowired
    public TaskItemManageController(TaskItemService service, TaskCardService cardService, GradeRateService rateService) {
        this.service = service;
        this.cardService = cardService;
        this.rateService = rateService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "新增评测卡指标", buildDetail = TaskItemLogDetailBuilder.SaveBuilder.class)
    @ApiOperation("新增评测卡指标")
    public ResultVo<TaskItemVo> save(@Valid @RequestBody TaskItemSaveForm form){
        TaskItem t = service.save(form.toDomain());
        return ResultVo.success(new TaskItemVo(t));
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改评测卡指标", buildDetail = TaskItemLogDetailBuilder.UpdateBuilder.class)
    @ApiOperation("修改评测卡指标")
    public ResultVo<TaskItemVo> update(@Valid @RequestBody TaskItemUpdateForm form){
        TaskItem t = service.update(form.toDomain());
        return ResultVo.success(new TaskItemVo(t));
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "删除评测卡指标", buildDetail = TaskItemLogDetailBuilder.DeleteBuilder.class)
    @ApiOperation("删除评测卡指标")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        boolean ok = service.delete(id);
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到评测卡指标")
    public ResultVo<TaskItemVo> get(@PathVariable("id")String id){
        return ResultVo.success(new TaskItemVo(service.get(id)));
    }

    @PostMapping(value = "import", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "删除评测卡指标", buildDetail = TaskItemLogDetailBuilder.ImportBuilder.class)
    @ApiOperation("导入评测卡指标")
    public ResultVo<ImportVo> importItem(@RequestParam(value = "file") @ApiParam(value = "上传文件", required = true) MultipartFile file,
                                     @RequestParam(value = "cardId") @ApiParam(value = "评测卡编号", required = true) String cardId){

        TaskCard card = cardService.get(cardId);
        ExcelReader reader = new TaskItemExcelReader(card, service, rateService);

        try(InputStream inputStream= file.getInputStream()){
            ReadResult result = reader.read(inputStream);
            cardService.updateItemCount(cardId);
            cardService.updateScore(cardId);
            int errorCount = result.getErrorRows() == null? 0 : result.getErrorRows().size();
            ImportVo vo = new ImportVo(result.getCount(), errorCount, result.getErrorRows());
            return ResultVo.success(vo);
        }catch (IOException e){
            LOGGER.error("Import item fail throw={}", e.getMessage());
            return ResultVo.error(254, "导入任务卡失败");
        }
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
