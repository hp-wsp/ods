package com.ts.server.ods.evaluation.controller.manage;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.common.excel.ExcelReader;
import com.ts.server.ods.controller.form.BatchDeleteForm;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.evaluation.controller.manage.form.EvaItemSaveForm;
import com.ts.server.ods.evaluation.controller.manage.form.EvaItemUpdateForm;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.service.EvaItemService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 评测指标API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/evaluation/item")
@Api(value = "/manage/evaluation/item", tags = "评测指标API接口")
public class EvaItemManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaItemManageController.class);
    private final EvaItemService service;
    private final OptLogService optLogService;

    @Autowired
    public EvaItemManageController(EvaItemService service, OptLogService optLogService) {
        this.service = service;
        this.optLogService = optLogService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新增评测指标")
    public ResultVo<EvaItem> save(@Valid @RequestBody EvaItemSaveForm form){
        EvaItem t = service.save(form.toDomain());

        optLogService.save("新增评测指标", new String[]{"编号", "测评指标编号"},
                new String[]{t.getId(), t.getNum()}, getCredential().getUsername());

        return ResultVo.success(t);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改评测指标")
    public ResultVo<EvaItem> update(@Valid @RequestBody EvaItemUpdateForm form){
        EvaItem t = service.update(form.toDomain());

        optLogService.save("修改评测指标", new String[]{"编号", "测评指标编号"},
                new String[]{t.getId(), t.getNum()}, getCredential().getUsername());

        return ResultVo.success(t);
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到评测指标")
    public ResultVo<EvaItem> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("删除评测指标")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        EvaItem t = service.get(id);

        boolean ok = service.delete(id);
        if(ok){
            optLogService.save("删除评测指标", new String[]{"编号", "测评指标编号"},
                    new String[]{t.getId(), t.getNum()}, getCredential().getUsername());
        }

        return ResultVo.success(new OkVo(ok));
    }

    @PutMapping(value = "batchDelete", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("批量删除指标")
    public ResultVo<OkVo> batchDelete(@Validated @RequestBody BatchDeleteForm form){
        for(String id: form.getIds()){
            delete(id);
        }
        return ResultVo.success(new OkVo(true));
    }

    @PostMapping(value = "import", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("导入测评指标")
    public ResultVo<OkVo> importItem(@RequestParam(value = "file") @ApiParam(value = "上传文件", required = true) MultipartFile file,
                                     @RequestParam(value = "evaId") @ApiParam(value = "测评编号", required = true) String evaId){

        ExcelReader reader = buildImportExcelReader(evaId);

        try(InputStream inputStream= file.getInputStream()){
            reader.read(inputStream);

            optLogService.save("导入测评指标", new String[]{"测评编号"},
                    new String[]{evaId}, getCredential().getUsername());

            return ResultVo.success(new OkVo(true));
        }catch (IOException e){
            LOGGER.error("Import item fail throw={}", e.getMessage());
            return ResultVo.error(253, "导入测评指标失败");
        }
    }

    private ExcelReader buildImportExcelReader(String evaId){
        return new ExcelReader((i, r) -> {
            String num = getCellContent(r, 1);

            boolean isHeader = i == 0 && StringUtils.equals(StringUtils.trim(StringUtils.remove(num, ' ')), "指标");
            if(isHeader){
                return ;
            }

            if(StringUtils.isBlank(num)){
                LOGGER.warn("Import row num is blank");
                return ;
            }

            EvaItem t = new EvaItem();
            t.setEvaId(evaId);
            t.setNum(num);
            t.setRequire(getCellContent(r, 2));
            t.setGrade(getCellContent(r, 3));
            String resultStr = getCellContent(r, 4);
            LOGGER.debug("Import excel index={},result={}", i, resultStr);
            String[] results = Arrays.stream(StringUtils.split(resultStr, " ")).map(StringUtils::trim)
                    .filter(e -> e.length() > 0).toArray(String[]::new);
            t.setResults(results);
            t.setRemark(getCellContent(r, 5));

            EvaItem item = service.importItem(t);
            LOGGER.debug("Import item={}", item);
        });
    }

    private String getCellContent(Row row, int col){
        String s = row.getCell(col).getRichStringCellValue().getString();
        s = StringUtils.replaceChars(s, '\n', ' ');
        s = StringUtils.replaceChars(s, '\t', ' ');
        s = StringUtils.trim(s);
        return s;
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

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }

}
