package com.ts.server.ods.controller.main.declare;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.main.declare.logger.BaseDeclareLogDetailBuilder;
import com.ts.server.ods.controller.main.declare.form.MemberInfoForm;
import com.ts.server.ods.controller.form.PasswordUpdateForm;
import com.ts.server.ods.controller.main.vo.MainDateVo;
import com.ts.server.ods.controller.main.vo.MainStatsVo;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.taskcard.service.TaskCardService;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 申报端基础API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/declare/account")
@ApiACL("ROLE_DECLARATION")
@Api(value = "/declare/account", tags = "申报端基础API接口")
public class AccountDecController {

    private final MemberService memberService;
    private final EvaluationService evaluationService;
    private final TaskCardService taskCardService;

    @Autowired
    public AccountDecController(MemberService memberService, EvaluationService evaluationService,
                                TaskCardService taskCardService) {

        this.memberService = memberService;
        this.evaluationService = evaluationService;
        this.taskCardService = taskCardService;
    }


    @PostMapping(value = "updatePassword", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改申报员密码", buildDetail = BaseDeclareLogDetailBuilder.UpdatePasswordBuilder.class)
    @ApiOperation("修改申报员密码")
    public ResultVo<OkVo> updatePassword(@Validated @RequestBody PasswordUpdateForm form){
        return ResultVo.success(new OkVo(memberService.updatePassword(getCredential().getId(), form.getPassword(), form.getNewPassword())));
    }

    @PutMapping(value = "account", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改管理员信息", buildDetail = BaseDeclareLogDetailBuilder.UpdateAccountBuilder.class)
    @ApiOperation("修改申报员信息")
    public ResultVo<Member> updateAccount(@Validated @RequestBody MemberInfoForm form){
        Member m = memberService.get(getCredential().getId());
        m.setName(form.getName());

        return ResultVo.success(memberService.update(m));
    }

    @GetMapping(value = "statistics", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("首页统计信息")
    public ResultVo<MainStatsVo> statistics(){
        Optional<Evaluation> optional = evaluationService.queryLasted();

        MainStatsVo vo = optional.map(e -> {
            Map<String, Integer> stats = taskCardService.queryGroupStatus(e.getId());
            int total = stats.values().stream().mapToInt(Integer::intValue).sum();

            Map<String, Integer> rate = new HashMap<>(5);
            if(total == 0){
                rate.put("WAIT", 0);
                rate.put("SUBMIT", 0);
                rate.put("GRADE", 0);
            }else{
                rate.put("WAIT", stats.getOrDefault("WAIT", 0) * 100 / total);
                rate.put("SUBMIT", (stats.getOrDefault("SUBMIT", 0) + stats.getOrDefault("BACK", 0)) * 100 / total);
                rate.put("GRADE", stats.getOrDefault("GRADE", 0) * 100 / total);
            }

            return new MainStatsVo(e, total, rate);
        }).orElse(new MainStatsVo(null, -1, null));

        return ResultVo.success(vo);
    }

    @GetMapping(value = "dateLine", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResultVo<Collection<MainDateVo>> dataLine(){
//        Optional<Evaluation> optional = evaluationService.queryLasted();
//        if(!optional.isPresent()){
//            return ResultVo.success(Collections.emptyList());
//        }
//
//        List<EvaluationLog> logs = evaluationService.queryLog(optional.get().getId());
//        Map<String, MainDateVo> data = new LinkedHashMap<>();
//        for(EvaluationLog log: logs){
//            MainDateVo vo = data.get(log.getDay());
//            if(vo == null){
//                vo = new MainDateVo(log.getDay());
//                data.put(log.getDay(), vo);
//            }
//            vo.addDetail(new MainDateVo.DateDetail(log.getDetail(), log.getUsername(), log.getCreateTime()));
//        }

        return ResultVo.success(Collections.emptyList());
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
