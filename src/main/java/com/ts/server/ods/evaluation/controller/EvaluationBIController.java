package com.ts.server.ods.evaluation.controller;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.OdsProperties;
import com.ts.server.ods.base.service.ResourceService;
import com.ts.server.ods.common.excel.ExcelWriter;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.domain.Declaration;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.service.DeclarationService;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.controller.vo.EvaluationItemVo;
import com.ts.server.ods.evaluation.controller.vo.ExportProgressVo;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaItemService;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.evaluation.service.runner.ExportResourceRunner;
import com.ts.server.ods.exec.OdsExecutorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/manage/bi/evaluation")
@Api(value = "/manage/bi/evaluation", tags = "评测汇编API接口")
public class EvaluationBIController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationBIController.class);
    private final EvaluationService evaluationService;
    private final EvaItemService evaItemService;
    private final DeclarationService declarationService;
    private final ResourceService resourceService;
    private final TaskCardService taskCardService;
    private final OdsExecutorService executorService;
    private final OdsProperties properties;

    @Autowired
    public EvaluationBIController(EvaluationService evaluationService, EvaItemService evaItemService,
                                  DeclarationService declarationService, ResourceService resourceService,
                                  TaskCardService taskCardService, OdsExecutorService executorService,
                                  OdsProperties properties) {

        this.evaluationService = evaluationService;
        this.evaItemService = evaItemService;
        this.declarationService = declarationService;
        this.resourceService = resourceService;
        this.taskCardService = taskCardService;
        this.executorService = executorService;
        this.properties = properties;
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询测评汇编")
    public ResultPageVo<EvaluationItemVo> query(@RequestParam("id")String id,
                                                @RequestParam(value = "num", required = false)String num,
                                                @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
                                                @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
                                                @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        List<EvaItem> items = evaItemService.query(id, num, "", page * rows, rows);
        List<EvaluationItemVo> itemLst = new ArrayList<>(items.size());
        for(int i = 0; i < items.size(); i++){
            itemLst.add(buildItem(items.get(i), page * rows + i));
        }

        return new ResultPageVo.Builder<>(page, rows, itemLst)
                .count(isCount, () -> evaItemService.count(id, num, ""))
                .setExt(buildQueryExt(id))
                .build();
    }

    private Map<String, String> buildQueryExt(String id){
        String status = "NONE";
        String exportId = "";
        int progress = executorService.progress(id);
        if(progress >= 0 && progress < 100){
            status = "EXPORTING";
        }else{
            Evaluation t = evaluationService.get(id);
            if(StringUtils.isNotBlank(t.getExportId())){
                status = "EXPORTED";
                exportId = t.getExportId();
            }
        }

        Map<String, String> ext = new LinkedHashMap<>(3);
        ext.put("stats", status);
        ext.put("exportId", exportId);

        return ext;
    }

    private EvaluationItemVo buildItem(EvaItem item, int index){
        return new EvaluationItemVo(index, item.getNum(), item.getRequire(), item.getGrade(), buildResources(item.getId()));
    }

    private List<EvaluationItemVo.Resource> buildResources(String itemId){
        List<Declaration> declarations = declarationService.queryByEvaItemId(itemId);

        return declarations.stream()
                .map(e -> new EvaluationItemVo.Resource(e.getId(), e.getFileName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value ="export")
    @ApiOperation("导出测评项目")
    public void export(@RequestParam @ApiParam(value = "测评编号", required = true) String id,
                       @RequestParam(defaultValue = "2003") @ApiParam(value = "格式", defaultValue = "2003") String style,
                       HttpServletResponse response){

        boolean is2003 = StringUtils.equals(style, "2003");
        Evaluation evaluation = evaluationService.get(id);
        String filename = evaluation.getName() + "材料汇编";
        response.setHeader("Content-Disposition", "attachment; filename*=" + buildFilename(filename, is2003) );
        response.setContentType(getContentType(is2003));

        ExcelWriter<EvaluationItemVo> writer = newWrite(is2003);
        final int rows = 100;

        try(OutputStream outputStream = response.getOutputStream()){
            for(int i = 0; i < 1000; i++){
                List<EvaItem> items = evaItemService.query(id, "", "", i * rows, rows);
                if(items.isEmpty()){
                    break;
                }

                List<EvaluationItemVo> itemLst = new ArrayList<>(items.size());
                for(int j = 0; j < items.size(); j++){
                    itemLst.add(buildItem(items.get(j), i * rows + j));
                }
                writer.write(outputStream, i * rows, itemLst);
            }
            outputStream.flush();
        }catch (IOException e){
            LOGGER.error("Exception evaluation fail id={}, throws={}", id, e.getMessage());
            throw new BaseException("导出汇总失败");
        }
    }

    private String buildFilename(String filename, boolean is2003){
        try{
            final String charset = "UTF-8";
            String suffer = is2003? "xls": "xlsx";
            return charset + "''"+ URLEncoder.encode(filename, charset) + "." + suffer;
        }catch (UnsupportedEncodingException e){
            return "";
        }
    }

    private String getContentType(boolean is2003){
        return is2003?
                "application/vnd.ms-excel":
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    private ExcelWriter<EvaluationItemVo> newWrite(boolean is2003){
            List<String> headData = Arrays.asList("序号", "指标", "具体要求", "评分标准", "材料");
            return new ExcelWriter<>((r, t) -> {
                Cell cell0 = r.createCell(0);
                cell0.setCellValue(t.getIndex() + 1);
                Cell cell1 = r.createCell(1);
                cell1.setCellValue(t.getNum());
                Cell cell2 = r.createCell(2);
                cell2.setCellValue(t.getRequire());
                Cell cell3 = r.createCell(3);
                cell3.setCellValue(t.getGrade());
                String fileNames = t.getResources().stream()
                        .map(EvaluationItemVo.Resource::getFilename)
                        .collect(Collectors.joining("\n"));
                Cell cell4 = r.createCell(4);
                cell4.setCellValue(fileNames);
            }, is2003, headData);
    }

    @GetMapping(value = "exportResource", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("导出测评资源")
    public ResultVo<OkVo> exportResource(@RequestParam(value = "id") @ApiParam(value = "测评编号", required = true) String id,
                                         @RequestParam(value = "password", required = false) @ApiParam(value = "密码")String password){
        Evaluation t = evaluationService.get(id);
        if(t.getStatus() != Evaluation.Status.OPEN){
            throw new BaseException("测评未开启");
        }

        executorService.submit(buildTaskKey(id), "测评资源导出",
                new ExportResourceRunner(evaluationService, evaItemService,
                        declarationService, resourceService, properties, id, password));

        return ResultVo.success(new OkVo(true));
    }

    private String buildTaskKey(String id){
        return "updateExport-" + id;
    }

    @GetMapping(value = "exportProgress", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到导出资源进度")
    public ResultVo<ExportProgressVo> exportProgress(@RequestParam(value = "id") @ApiParam(value = "测评编号", required = true) String id){
        int progress = executorService.progress(buildTaskKey(id));
        String exportId = "";
        if(progress >= 100){
            Evaluation t = evaluationService.get(id);
            exportId = t.getExportId();
        }
        return ResultVo.success(new ExportProgressVo(progress, exportId));
    }

    @GetMapping(value = "score", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("测评分数报表")
    public ResultPageVo<TaskCard> queryGradeScore(@RequestParam String id,
                                                  @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
                                                  @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
                                                  @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        List<TaskCard> cards = taskCardService.queryGrade(id, "", page * rows, rows);

        return new ResultPageVo.Builder<>(page, rows, cards)
                .count(isCount, () -> taskCardService.countGrade(id, ""))
                .setExt(buildQueryExt(id))
                .build();
    }

    @GetMapping(value ="exportScore")
    @ApiOperation("导出测评分数排名")
    public void exportScore(@RequestParam @ApiParam(value = "测评编号", required = true) String id,
                            @RequestParam(defaultValue = "2003") @ApiParam(value = "格式", defaultValue = "2003") String style,
                            HttpServletResponse response){

        boolean is2003 = StringUtils.equals(style, "2003");
        Evaluation evaluation = evaluationService.get(id);
        String filename = evaluation.getName() + "分数排名";
        response.setHeader("Content-Disposition", "attachment; filename*=" + buildFilename(filename, is2003) );
        response.setContentType(getContentType(is2003));

        ExcelWriter<TaskCard> writer = newWriteScore(is2003);
        final int rows = 100;

        try(OutputStream outputStream = response.getOutputStream()){
            for(int i = 0; i < 1000; i++){
                List<TaskCard> cards = taskCardService.queryGrade(id, "", i * rows, rows);
                if(cards.isEmpty()){
                    break;
                }
                writer.write(outputStream, i * rows, cards);
            }
            outputStream.flush();
        }catch (IOException e){
            LOGGER.error("Exception evaluation fail id={}, throws={}", id, e.getMessage());
            throw new BaseException("导出汇总失败");
        }
    }

    private ExcelWriter<TaskCard> newWriteScore(boolean is2003){
        List<String> headData = Arrays.asList("序号", "单位", "分数");
        return new ExcelWriter<>((r, t) -> {
            Cell cell0 = r.createCell(0);
            cell0.setCellValue(r.getRowNum());
            Cell cell1 = r.createCell(1);
            cell1.setCellValue(t.getCompanyName());
            Cell cell2 = r.createCell(2);
            cell2.setCellValue(formatScore(t.getGradeScore()));
        }, is2003, headData);
    }

    private String formatScore(int score){
        int remain = score % 100;
        return  remain == 0? String.valueOf(score / 100): String.format("%d.%02d", score/ 100, remain);
    }
}
