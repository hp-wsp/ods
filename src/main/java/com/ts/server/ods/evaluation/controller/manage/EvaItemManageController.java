package com.ts.server.ods.evaluation.controller.manage;

import com.ts.server.ods.common.excel.reader.ExcelReader;
import com.ts.server.ods.common.excel.reader.ReadResult;
import com.ts.server.ods.controller.form.BatchDeleteForm;
import com.ts.server.ods.controller.vo.ImportVo;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.evaluation.controller.manage.excel.EvaItemExcelReader;
import com.ts.server.ods.evaluation.controller.manage.form.EvaItemSaveForm;
import com.ts.server.ods.evaluation.controller.manage.form.EvaItemUpdateForm;
import com.ts.server.ods.evaluation.controller.manage.logger.EvaItemLogDetailBuilder;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.service.EvaItemService;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 评测指标API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/evaItem")
@Api(value = "/manage/evaItem", tags = "评测指标API接口")
public class EvaItemManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaItemManageController.class);
    private final EvaItemService service;

    @Autowired
    public EvaItemManageController(EvaItemService service) {
        this.service = service;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "新增评测指标", buildDetail = EvaItemLogDetailBuilder.SaveBuilder.class)
    @ApiOperation("新增评测指标")
    public ResultVo<EvaItem> save(@Valid @RequestBody EvaItemSaveForm form){
        EvaItem t = service.save(form.toDomain());
        return ResultVo.success(t);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改评测指标", buildDetail = EvaItemLogDetailBuilder.UpdateBuilder.class)
    @ApiOperation("修改评测指标")
    public ResultVo<EvaItem> update(@Valid @RequestBody EvaItemUpdateForm form){
        EvaItem t = service.update(form.toDomain());
        return ResultVo.success(t);
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到评测指标")
    public ResultVo<EvaItem> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改评测指标", buildDetail = EvaItemLogDetailBuilder.DeleteBuilder.class)
    @ApiOperation("删除评测指标")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        boolean ok = service.delete(id);
        return ResultVo.success(new OkVo(ok));
    }

    @PutMapping(value = "batchDelete", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "批量删除指标", buildDetail = EvaItemLogDetailBuilder.BatchDeleteBuilder.class)
    @ApiOperation("批量删除指标")
    public ResultVo<OkVo> batchDelete(@Validated @RequestBody BatchDeleteForm form){
        for(String id: form.getIds()){
            delete(id);
        }
        return ResultVo.success(new OkVo(true));
    }

    @PostMapping(value = "import", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "导入测评指标", buildDetail = EvaItemLogDetailBuilder.ImportBuilder.class)
    @ApiOperation("导入测评指标")
    public ResultVo<ImportVo> importItem(@RequestParam(value = "file") @ApiParam(value = "上传文件", required = true) MultipartFile file,
                                         @RequestParam(value = "evaId") @ApiParam(value = "测评编号", required = true) String evaId){

        ExcelReader reader = new EvaItemExcelReader(evaId, service);
        try(InputStream inputStream= file.getInputStream()){
            ReadResult result = reader.read(inputStream);
            int errorCount = result.getErrorRows() == null? 0 : result.getErrorRows().size();
            ImportVo vo = new ImportVo(result.getCount(), errorCount, result.getErrorRows());
            return ResultVo.success(vo);
        }catch (IOException e){
            LOGGER.error("Import item fail throw={}", e.getMessage());
            return ResultVo.error(253, "导入测评指标失败");
        }
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询评测指标")
    public ResultPageVo<EvaItem> query(
            @ApiParam(value = "测评编号") @RequestParam(required = false) String evaId,
            @ApiParam(value = "具体要求") @RequestParam(required = false) String require,
            @ApiParam(value = "评测编号") @RequestParam(required = false) String num,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        return new ResultPageVo.Builder<>(page, rows, service.query( evaId, num, require,page * rows, rows))
                .count(isCount, () -> service.count(evaId, num, require))
                .build();
    }

}
