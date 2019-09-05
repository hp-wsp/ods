package com.ts.server.ods.evaluation.controller;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.SmsProperties;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.controller.form.EvaluationSaveForm;
import com.ts.server.ods.evaluation.controller.form.EvaluationUpdateForm;
import com.ts.server.ods.evaluation.controller.vo.ExportProgressVo;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.evaluation.service.runner.LaunchSmsRunner;
import com.ts.server.ods.exec.OdsExecutorService;
import com.ts.server.ods.logger.service.OptLogService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import com.ts.server.ods.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 评测API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/evaluation/info")
@Api(value = "/manage/evaluation/info", tags = "测评API接口")
public class EvaluationController {
    private final EvaluationService service;
    private final TaskCardService taskCardService;
    private final MemberService memberService;
    private final SmsService smsService;
    private final OdsExecutorService executorService;
    private final OptLogService optLogService;
    private final SmsProperties properties;

    @Autowired
    public EvaluationController(EvaluationService service, TaskCardService taskCardService,
                                MemberService memberService, SmsService smsService,
                                OdsExecutorService executorService, OptLogService optLogService,
                                SmsProperties properties) {

        this.service = service;
        this.taskCardService = taskCardService;
        this.memberService = memberService;
        this.smsService = smsService;
        this.executorService = executorService;
        this.optLogService = optLogService;
        this.properties = properties;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新增评测")
    public ResultVo<Evaluation> save(@Valid @RequestBody EvaluationSaveForm form){
        Credential credential = getCredential();
        Evaluation evaluation = service.save(form.toDomain(), form.getImportId(), form.isImportTask(), credential.getUsername());

        optLogService.save("新增评测", new String[]{"编号", "名称"},
                new String[]{evaluation.getId(), evaluation.getName()}, getCredential().getUsername());

        return ResultVo.success(evaluation);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改评测")
    public ResultVo<Evaluation> update(@Valid @RequestBody EvaluationUpdateForm form){
        Evaluation evaluation = service.update(form.toDomain());

        optLogService.save("修改评测", new String[]{"编号", "名称"},
                new String[]{evaluation.getId(), evaluation.getName()}, getCredential().getUsername());

        return ResultVo.success(evaluation);
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到评测")
    public ResultVo<Evaluation> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("删除评测")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        Evaluation evaluation = service.get(id);

        boolean ok = service.delete(id);
        if(ok){
            optLogService.save("删除评测", new String[]{"编号", "名称"},
                    new String[]{evaluation.getId(), evaluation.getName()}, getCredential().getUsername());
        }
        return ResultVo.success(new OkVo(ok));
    }

    @PutMapping(value = "open/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("开启评测")
    public ResultVo<Evaluation> open(@PathVariable("id")String id){
        Credential credential = getCredential();
        Evaluation evaluation = service.updateStatus(id, Evaluation.Status.OPEN, credential.getUsername());

        optLogService.save("开启评测", new String[]{"编号", "名称"},
                new String[]{evaluation.getId(), evaluation.getName()}, credential.getUsername());

        return ResultVo.success(evaluation);
    }

    @PutMapping(value = "close/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("关闭评测")
    public ResultVo<Evaluation> close(@PathVariable("id")String id){
        Credential credential = getCredential();
        Evaluation evaluation = service.updateStatus(id, Evaluation.Status.CLOSE, credential.getUsername());

        optLogService.save("关闭评测", new String[]{"编号", "名称"},
                new String[]{evaluation.getId(), evaluation.getName()}, credential.getUsername());

        return ResultVo.success(evaluation);
    }

    @PutMapping(value = "openDec/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("开启申报")
    public ResultVo<Evaluation> openDec(@PathVariable("id")String id){
        Credential credential = getCredential();
        Evaluation evaluation = service.openDec(id, credential.getUsername());

        optLogService.save("开启申报", new String[]{"编号", "名称"},
                new String[]{evaluation.getId(), evaluation.getName()}, credential.getUsername());

        return ResultVo.success(evaluation);
    }

    @PutMapping(value = "closeDec/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("开启申报")
    public ResultVo<Evaluation> closeDec(@PathVariable("id")String id){
        Credential credential = getCredential();
        Evaluation evaluation = service.closeDec(id, credential.getUsername());

        optLogService.save("关闭申报", new String[]{"编号", "名称"},
                new String[]{evaluation.getId(), evaluation.getName()}, credential.getUsername());

        return ResultVo.success(evaluation);
    }

    @GetMapping(value = "sendStartSms/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("发送测评开始短信")
    public ResultVo<OkVo> sendStartSms(@PathVariable("id")String id){
        executorService.submit(buildTaskKey(id), "发送测评开启短信",
                new LaunchSmsRunner(service, taskCardService, memberService, smsService, properties, id));

        Evaluation evaluation = service.get(id);
        optLogService.save("发送测评开启短信", new String[]{"编号", "名称"},
                new String[]{evaluation.getId(), evaluation.getName()}, getCredential().getUsername());

        return ResultVo.success(new OkVo(true));
    }

    private String buildTaskKey(String id){
        return "evaSms-" + id;
    }

    @GetMapping(value = "smsProgress", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("发送短信进度")
    public ResultVo<ExportProgressVo> smsProgress(@RequestParam(value = "id") @ApiParam(value = "测评编号", required = true) String id){
        return ResultVo.success(new ExportProgressVo(executorService.progress(buildTaskKey(id))));
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询评测")
    public ResultPageVo<Evaluation> query(
            @ApiParam(value = "名称") @RequestParam(required = false) String name,
            @ApiParam(value = "状态") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        Evaluation.Status s =   StringUtils.isBlank(status)? null: Evaluation.Status.valueOf(status);
        return new ResultPageVo.Builder<>(page, rows, service.query( name, s,page * rows, rows))
                .count(isCount, () -> service.count( name, s))
                .build();
    }

    @GetMapping(value = "active", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询激活评测列表")
    public ResultVo<List<Evaluation>> queryActive(){
        return ResultVo.success(service.queryActive());
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
