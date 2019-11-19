package com.ts.server.ods.etask.controller.declare;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.controller.manage.vo.TaskCardVo;
import com.ts.server.ods.etask.domain.Declaration;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.service.DeclarationService;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.etask.service.TaskItemService;
import com.ts.server.ods.logger.service.OptLogService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 申报服务API
 *
 * @author <a href="mailto:hhywangwei@cgmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/declare")
@Api(value = "/declare", tags = "申报资料服务API接口")
public class DeclarationController {
    private final TaskCardService cardService;
    private final TaskItemService itemService;
    private final DeclarationService decService;
    private final MemberService memberService;
    private final OptLogService optLogService;

    @Autowired
    public DeclarationController(TaskCardService cardService, TaskItemService itemService,
                                 DeclarationService decService, MemberService memberService,
                                 OptLogService optLogService) {

        this.cardService = cardService;
        this.itemService = itemService;
        this.decService = decService;
        this.memberService = memberService;
        this.optLogService = optLogService;
    }

    @PostMapping(value = "resource",consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新增申报资源")
    public ResultVo<Declaration> save(@RequestParam(value = "file") @ApiParam(value = "上传文件", required = true) MultipartFile file,
                                      @RequestParam(value = "taskItemId") @ApiParam(value = "任务指标编号") String taskItemId){

        Credential credential = getCredential();

        String filename = file.getOriginalFilename();

        if(StringUtils.isBlank(filename)){
            throw new BaseException("文件名不能为空");
        }

        if(StringUtils.length(filename) > 64){
            throw new BaseException("文件名不能超过64个字符");
        }

        Declaration t = decService.save(taskItemId, credential.getId(), file);

        optLogService.save("新增申报资源", new String[]{"编号", "文件名"},
                new String[]{t.getId(), t.getFileName()}, getCredential().getUsername());

        return ResultVo.success(t);
    }

    @DeleteMapping(value = "resource/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("删除申报资源")
    public ResultVo<OkVo> delete(@ApiParam("申报资源编号") @PathVariable("id")String id){
        Declaration t = decService.get(id);

        boolean ok = decService.delete(id, getCredential().getId());
        if(ok){
            optLogService.save("删除申报资源", new String[]{"编号", "文件名"},
                    new String[]{t.getId(), t.getFileName()}, getCredential().getUsername());
        }

        return ResultVo.success(new OkVo(ok));
    }

    @PutMapping(value = "submit/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("提交评测任务")
    public ResultVo<TaskCard> submit(@ApiParam("任务卡编号") @PathVariable("id")String id){
        Member member = memberService.get(getCredential().getId());
        TaskCard card = cardService.submit(id, member);

        optLogService.save("提交评测任务", new String[]{"编号", "单位"},
                new String[]{card.getId(), card.getCompanyName()}, getCredential().getUsername());

        return ResultVo.success(card);
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("获取申报员任务卡")
    public ResultVo<List<TaskCard>> queryCard(){
        return ResultVo.success(cardService.queryOpenByDecId(getCredential().getId()));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到任务卡详细信息")
    public ResultVo<TaskCardVo> getCardDetail(@PathVariable("id")String id){
        TaskCard card = cardService.get(id);

        if(!card.isOpen()){
            throw new BaseException("评测任务未开启");
        }

        if(!StringUtils.equals(card.getDecId(), getCredential().getId())){
            throw new BaseException("无查看权限");
        }

        Map<String, List<Declaration>> map = decService.queryByCardId(id).stream()
                .collect(Collectors.groupingBy(Declaration::getCardItemId));
        List<TaskCardVo.CardItemVo> items = itemService.queryByCardId(id).stream()
                .map(e -> new TaskCardVo.CardItemVo(e, map.getOrDefault(e.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        return ResultVo.success(new TaskCardVo(card, items));
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
