package com.ts.server.ods.evaluation.controller.manage;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.OdsProperties;
import com.ts.server.ods.base.service.ResourceService;
import com.ts.server.ods.common.excel.writer.ExcelWriter;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.taskcard.domain.Declaration;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.taskcard.service.DeclarationService;
import com.ts.server.ods.taskcard.service.TaskCardService;
import com.ts.server.ods.evaluation.controller.manage.excel.EvaCollectExcelWriter;
import com.ts.server.ods.evaluation.controller.manage.excel.EvaScoreExcelWriter;
import com.ts.server.ods.evaluation.controller.manage.vo.EvaluationItemVo;
import com.ts.server.ods.evaluation.controller.manage.vo.ExportProgressVo;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaItemService;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.evaluation.controller.manage.runner.ExportResourceRunner;
import com.ts.server.ods.exec.OdsExecutorService;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 测评汇编API接口
 *
 * <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/evaBi")
@ApiACL({"ROLE_SYS"})
@Api(value = "/manage/evaBi", tags = "评测汇编API接口")
public class EvaBiManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaBiManageController.class);
    private static final int BATCH_ROWS = 100;

    private final EvaluationService evaluationService;
    private final EvaItemService evaItemService;
    private final DeclarationService declarationService;
    private final ResourceService resourceService;
    private final TaskCardService taskCardService;
    private final OdsExecutorService executorService;
    private final OdsProperties properties;

    @Autowired
    public EvaBiManageController(EvaluationService evaluationService, EvaItemService evaItemService,
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

    @GetMapping(value = "collect", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询测评汇编")
    public ResultPageVo<EvaluationItemVo> queryCollect(@RequestParam("id")String id,
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

    @GetMapping(value ="expCollect")
    @ApiOperation("导出测评项目")
    public void expCollect(@RequestParam @ApiParam(value = "测评编号", required = true) String id,
                           @RequestParam(defaultValue = "2003") @ApiParam(value = "格式", defaultValue = "2003") String style,
                           HttpServletResponse response){

        boolean is2003 = StringUtils.equals(style, "2003");
        Evaluation evaluation = evaluationService.get(id);
        String filename = evaluation.getName() + "材料汇编";

        try(ExcelWriter<EvaluationItemVo> writer = new EvaCollectExcelWriter(response, is2003, filename)){
            for(int i = 0; i < 1000; i++){
                List<EvaItem> items = evaItemService.query(id, "", "", i * BATCH_ROWS, BATCH_ROWS);
                if(items.isEmpty()){
                    break;
                }

                List<EvaluationItemVo> itemLst = new ArrayList<>(items.size());
                for(int j = 0; j < items.size(); j++){
                    itemLst.add(buildItem(items.get(j), i * BATCH_ROWS + j));
                }
                writer.write(i * BATCH_ROWS, itemLst);
            }
        }catch (IOException e){
            LOGGER.error("Exception evaluation fail id={}, throws={}", id, e.getMessage());
            throw new BaseException("导出汇总失败");
        }
    }

    @GetMapping(value = "score", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("测评分数报表")
    public ResultPageVo<TaskCard> queryScore(@RequestParam String id,
                                             @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
                                             @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
                                             @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        List<TaskCard> cards = taskCardService.queryGrade(id, "", page * rows, rows);

        return new ResultPageVo.Builder<>(page, rows, cards)
                .count(isCount, () -> taskCardService.countGrade(id, ""))
                .setExt(buildQueryExt(id))
                .build();
    }

    @GetMapping(value ="expScore")
    @ApiOperation("导出测评分数排名")
    public void exportScore(@RequestParam @ApiParam(value = "测评编号", required = true) String id,
                            @RequestParam(defaultValue = "2003") @ApiParam(value = "格式", defaultValue = "2003") String style,
                            HttpServletResponse response){

        boolean is2003 = StringUtils.equals(style, "2003");
        Evaluation evaluation = evaluationService.get(id);
        String filename = evaluation.getName() + "分数排名";

        try(ExcelWriter<TaskCard> writer = new EvaScoreExcelWriter(response, is2003, filename)){
            for(int i = 0; i < 1000; i++){
                List<TaskCard> cards = taskCardService.queryGrade(id, "", i * BATCH_ROWS, BATCH_ROWS);
                if(cards.isEmpty()){
                    break;
                }
                writer.write(i * BATCH_ROWS, cards);
            }
        }catch (IOException e){
            LOGGER.error("Exception evaluation fail id={}, throws={}", id, e.getMessage());
            throw new BaseException("导出汇总失败");
        }
    }

    @GetMapping(value = "expResource", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("导出测评资源")
    public ResultVo<OkVo> expResource(@RequestParam(value = "id") @ApiParam(value = "测评编号", required = true) String id,
                                         @RequestParam(value = "password", required = false) @ApiParam(value = "密码")String password){
        Evaluation t = evaluationService.get(id);
        if(t.getStatus() != Evaluation.Status.OPEN){
            throw new BaseException("测评未开启");
        }

        executorService.submit(buildTaskKey(id), "测评资源导出",
                new ExportResourceRunner(evaluationService, evaItemService,
                        declarationService, resourceService, properties, id));

        return ResultVo.success(new OkVo(true));
    }

    private String buildTaskKey(String id){
        return "updateExport-" + id;
    }

    @GetMapping(value = "expResourcePro", produces = APPLICATION_JSON_UTF8_VALUE)
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
}
