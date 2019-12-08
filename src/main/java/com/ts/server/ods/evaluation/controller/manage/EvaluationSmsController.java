package com.ts.server.ods.evaluation.controller.manage;

import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.sms.SmsSender;
import com.ts.server.ods.taskcard.service.TaskCardService;
import com.ts.server.ods.evaluation.controller.manage.logger.EvaluationSmsLogDetailBuilder;
import com.ts.server.ods.evaluation.controller.manage.vo.ExportProgressVo;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.evaluation.runner.LaunchSmsRunner;
import com.ts.server.ods.exec.OdsExecutorService;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 测评短信API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/sms")
@ApiACL({"ROLE_SYS"})
@Api(value = "/manage/sms", tags = "测评短信API接口")
public class EvaluationSmsController {

    private final EvaluationService service;
    private final TaskCardService taskCardService;
    private final MemberService memberService;
    private final OdsExecutorService executorService;
    private final SmsSender smsSender;

    @Autowired
    public EvaluationSmsController(EvaluationService service, TaskCardService taskCardService,
                                   MemberService memberService, OdsExecutorService executorService,
                                   SmsSender smsSender) {

        this.service = service;
        this.taskCardService = taskCardService;
        this.memberService = memberService;
        this.executorService = executorService;
        this.smsSender = smsSender;
    }

    @GetMapping(value = "declare/send/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "发送测评短信", buildDetail = EvaluationSmsLogDetailBuilder.SendDeclareBuilder.class)
    @ApiOperation("发送开始评审测评短信")
    public ResultVo<OkVo> sendDeclare(@PathVariable("id")String id){
        executorService.submit(buildTaskKey(id), "发送测评开启短信",
                new LaunchSmsRunner(service, taskCardService, memberService, id, smsSender));
        return ResultVo.success(new OkVo(true));
    }

    private String buildTaskKey(String id){
        return "sms_send_declare-" + id;
    }

    @GetMapping(value = "declare/progress/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("发送开始评审测评短信进度")
    public ResultVo<ExportProgressVo> progressDeclare(@RequestParam(value = "id") @ApiParam(value = "测评编号", required = true) String id){
        return ResultVo.success(new ExportProgressVo(executorService.progress(buildTaskKey(id))));
    }
}
