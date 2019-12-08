package com.ts.server.ods.taskcard.controller.declare;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.main.declare.credential.DecCredential;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.taskcard.controller.declare.logger.TaskCardDecLogDetailBuilder;
import com.ts.server.ods.taskcard.controller.manage.vo.TaskCardVo;
import com.ts.server.ods.taskcard.domain.Declaration;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.taskcard.service.DeclarationService;
import com.ts.server.ods.taskcard.service.TaskCardService;
import com.ts.server.ods.taskcard.service.TaskItemService;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.CredentialContextUtils;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 申报测评卡API接口
 *
 * @author <a href="mailto:hhywangwei@cgmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/declare/card")
@ApiACL("ROLE_DECLARATION")
@Api(value = "/declare/card", tags = "申报测评卡API接口")
public class TaskCardDecController {
    private final TaskCardService cardService;
    private final TaskItemService itemService;
    private final DeclarationService decService;
    private final MemberService memberService;

    @Autowired
    public TaskCardDecController(TaskCardService cardService, TaskItemService itemService,
                                 DeclarationService decService, MemberService memberService) {

        this.cardService = cardService;
        this.itemService = itemService;
        this.decService = decService;
        this.memberService = memberService;
    }

    @PutMapping(value = "submit/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "提交评测任务", buildDetail = TaskCardDecLogDetailBuilder.SubmitBuilder.class)
    @ApiOperation("提交评测任务")
    public ResultVo<TaskCard> submit(@ApiParam("任务卡编号") @PathVariable("id")String id){
        Member member = memberService.get(getCredential().getId());
        TaskCard card = cardService.submit(id, member);
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

    private DecCredential getCredential(){
        return (DecCredential)CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
