package com.ts.server.ods.controller.main.mangae;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.base.service.ManagerService;
import com.ts.server.ods.controller.main.mangae.form.ManagerInfoForm;
import com.ts.server.ods.controller.main.form.PasswordUpdateForm;
import com.ts.server.ods.controller.main.mangae.logger.UpdateMangerAccountLogDetailBuilder;
import com.ts.server.ods.controller.main.vo.MainDateVo;
import com.ts.server.ods.controller.main.vo.MainStatsVo;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.domain.EvaluationLog;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 管理端基础API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage")
@Api(value = "/manage", tags = "管理端基础API接口")
public class BaseManageController {

    private final ManagerService managerService;
    private final EvaluationService evaluationService;
    private final TaskCardService taskCardService;

    @Autowired
    public BaseManageController(ManagerService managerService, EvaluationService evaluationService,
                                TaskCardService taskCardService) {

        this.managerService = managerService;
        this.evaluationService = evaluationService;
        this.taskCardService = taskCardService;
    }

    @PostMapping(value = "updatePassword", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改管理员密码")
    @ApiOperation("修改申报员密码")
    public ResultVo<OkVo> updatePassword(@Validated @RequestBody PasswordUpdateForm form){
        return ResultVo.success(new OkVo(managerService.updatePassword(getCredential().getId(), form.getPassword(), form.getNewPassword())));
    }

    @PutMapping(value = "account", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改管理员信息", buildDetail = UpdateMangerAccountLogDetailBuilder.class)
    @ApiOperation("修改管理员信息")
    public ResultVo<Manager> updateAccount(@Validated @RequestBody ManagerInfoForm form){
        Manager m = managerService.get(getCredential().getId());
        m.setName(form.getName());
        m.setPhone(form.getPhone());

        return ResultVo.success(managerService.update(m));
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
        Optional<Evaluation> optional = evaluationService.queryLasted();
        if(!optional.isPresent()){
            return ResultVo.success(Collections.emptyList());
        }

        List<EvaluationLog> logs = evaluationService.queryLog(optional.get().getId());
        Map<String, MainDateVo> data = new LinkedHashMap<>();
        for(EvaluationLog log: logs){
            MainDateVo vo = data.get(log.getDay());
            if(vo == null){
                vo = new MainDateVo(log.getDay());
                data.put(log.getDay(), vo);
            }
            vo.addDetail(new MainDateVo.DateDetail(log.getDetail(), log.getUsername(), log.getCreateTime()));
        }

        return ResultVo.success(data.values());
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
