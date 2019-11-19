package com.ts.server.ods.etask.controller.manage;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.controller.manage.form.GradeForm;
import com.ts.server.ods.etask.controller.manage.vo.TaskCardVo;
import com.ts.server.ods.etask.domain.Declaration;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.etask.service.DeclarationService;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.etask.service.TaskCardSmsService;
import com.ts.server.ods.etask.service.TaskItemService;
import com.ts.server.ods.logger.service.OptLogService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 评分API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/check")
@Api(value = "/manage/check", tags = "评分API接口")
public class GradeManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradeManageController.class);

    private final TaskCardService cardService;
    private final TaskItemService itemService;
    private final DeclarationService decService;
    private final TaskCardSmsService taskCardSmsService;
    private final OptLogService optLogService;

    @Autowired
    public GradeManageController(TaskCardService cardService, TaskItemService itemService,
                                 DeclarationService decService, TaskCardSmsService taskCardSmsService,
                                 OptLogService optLogService) {

        this.cardService = cardService;
        this.itemService = itemService;
        this.decService = decService;
        this.taskCardSmsService = taskCardSmsService;
        this.optLogService = optLogService;
    }

    @PutMapping(value = "grade", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("打分")
    public ResultVo<TaskItem> grade(@Valid @RequestBody GradeForm form){
        Credential credential = getCredential();
        TaskItem item = itemService.grade(form.getId(), form.getLevel(), form.getScore(), form.getRemark(), credential.getId());

        optLogService.save("打分", new String[]{"编号", "测评指标编号", "打分"},
                new String[]{item.getId(), item.getEvaNum(), item.getGradeLevel()}, getCredential().getUsername());

        return ResultVo.success(item);
    }

    @GetMapping(value = "urge/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("催促提交测评项目")
    public ResultVo<OkVo> urge(@RequestParam("id")String id, @RequestParam("content") String content){
        TaskCard card = cardService.get(id);

        if(!card.isOpenGrade()){
            throw new BaseException("测评未开启");
        }

        if(card.getStatus() != TaskCard.Status.WAIT && card.getStatus() != TaskCard.Status.BACK){
            throw new BaseException("已经提交处理");
        }

        taskCardSmsService.urge(cardService.get(id), content);

        return ResultVo.success(new OkVo(true));
    }

    @PutMapping(value = "back/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("退回")
    public ResultVo<TaskCard> back(@PathVariable("id")String id){
        Credential credential = getCredential();

        TaskCard card = cardService.get(id);
        if(!StringUtils.equals(card.getAssId(), credential.getId()) && !isSysRole(credential)){
            throw new BaseException("权限不够不");
        }

        TaskCard newCard = cardService.back(id, credential.getUsername());

        optLogService.save("退回评测", new String[]{"编号", "单位"},
                new String[]{newCard.getId(), newCard.getCompanyName()}, getCredential().getUsername());

        taskCardSmsService.back(newCard);
        return ResultVo.success(newCard);
    }

    @PutMapping(value = "cancelBack/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("撤回退回")
    public ResultVo<TaskCard> cancelBack(@PathVariable("id")String id){
        Credential credential = getCredential();

        TaskCard card = cardService.get(id);
        if(!StringUtils.equals(card.getAssId(), credential.getId()) && !isSysRole(credential)){
            throw new BaseException("权限不够不");
        }

        TaskCard newCard = cardService.cancelBack(id, credential.getUsername());

        optLogService.save("撤回退回评测", new String[]{"编号", "单位"},
                new String[]{newCard.getId(), newCard.getCompanyName()}, getCredential().getUsername());

        taskCardSmsService.cancelBack(newCard);
        return ResultVo.success(newCard);
    }

    @PutMapping(value = "finish/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("完成")
    public ResultVo<TaskCard> finish(@PathVariable("id")String id){
        Credential credential = getCredential();

        TaskCard card = cardService.get(id);
        if(!StringUtils.equals(card.getAssId(), credential.getId()) && !isSysRole(credential)){
            throw new BaseException("权限不够不");
        }

        TaskCard newCard = cardService.finish(id, credential.getUsername());
        optLogService.save("评分完成", new String[]{"编号", "单位"},
                new String[]{newCard.getId(), newCard.getCompanyName()}, getCredential().getUsername());

        return ResultVo.success(newCard);
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("获取评分员任务卡")
    public ResultPageVo<TaskCard> queryCard(@RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
                                            @RequestParam(required = false) @ApiParam(value = "单位名称") String companyName,
                                            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
                                            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        Credential credential = getCredential();
        String assId = isSysRole(credential)? "" : credential.getId();
        List<TaskCard> cards = cardService.queryOpenByAssId(assId, companyName, page * rows, rows);
        return new ResultPageVo.Builder<>(page, rows, cards)
                .count(isCount, () -> cardService.countOpenByAssId(assId, companyName))
                .build();
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到任务卡详细信息")
    public ResultVo<TaskCardVo> getCardDetail(@PathVariable("id")String id){
        TaskCard card = cardService.get(id);

        if(!card.isOpenGrade()){
            throw new BaseException("评测任务未开启");
        }

        Credential credential = getCredential();
        if(!isSysRole(credential) && !StringUtils.equals(card.getAssId(), credential.getId())){
            throw new BaseException("无查看权限");
        }

        Map<String, List<Declaration>> map = decService.queryByCardId(id).stream()
                .collect(Collectors.groupingBy(Declaration::getCardItemId));
        List<TaskCardVo.CardItemVo> items = itemService.queryByCardId(id).stream()
                .map(e -> new TaskCardVo.CardItemVo(e, map.getOrDefault(e.getId(), Collections.emptyList())))
                .sorted(Comparator.comparing(e -> formatNum(e.getItem().getEvaNum())))
                .collect(Collectors.toList());

        return ResultVo.success(new TaskCardVo(card, items));
    }

    private String formatNum(String num){
        String[] array = StringUtils.split(num, "-");
        if(array.length == 1){
            return array[0];
        }
        array[1] = StringUtils.length(array[1]) > 1? array[1]: "0" + array[1];
        String format = StringUtils.join(array, "-");
        LOGGER.debug("Format num src={},target={}", num, format);

        return format;
    }

    @GetMapping(value = "downland/{itemId}")
    @ApiOperation("下载测评卡资源")
    public void downland(@PathVariable("itemId") String itemId, HttpServletResponse response){
        TaskItem item = itemService.get(itemId);
        List<Declaration> declarations = decService.queryByItemId(itemId);
        if(declarations.isEmpty()){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        try(OutputStream outputStream = response.getOutputStream();
            ZipArchiveOutputStream zipStream = new ZipArchiveOutputStream(outputStream)){
            String zipFilename = item.getEvaNum() + ".zip";
            response.setHeader("Content-Disposition", "attachment; filename*=" + buildFilename(zipFilename));
            response.setContentType("application/force-download");
            for(Declaration t: declarations){
                LOGGER.debug("Get declaration resources declarationId={}, path={}, filename={},",
                        t.getId(), t.getPath(), t.getFileName());
                addFileToZip(zipStream, t.getPath(), t.getFileName());
            }
            outputStream.flush();
        }catch (IOException e){
            LOGGER.error("Downland item grade resources fail itemId={}, throw={}", itemId, e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private String buildFilename(String filename){
        try{
            final String charset = "UTF-8";
            return charset + "''"+ URLEncoder.encode(filename, charset);
        }catch (UnsupportedEncodingException e){
            return "";
        }
    }

    private static void addFileToZip(ZipArchiveOutputStream zOut, String path, String entryName) throws IOException {
        File f = new File(path);

        try (FileInputStream fInputStream = new FileInputStream(f)){
            ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
            entry.setSize(f.length());
            entry.setTime(System.currentTimeMillis());
            zOut.putArchiveEntry(entry);
            IOUtils.copy(fInputStream, zOut);
            zOut.closeArchiveEntry();
        }
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }

    private boolean isSysRole(Credential credential){
        return credential.getRoles().contains("ROLE_SYS");
    }
}
