package com.ts.server.ods.controller;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.domain.Resource;
import com.ts.server.ods.base.service.ManagerService;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.base.service.ResourceService;
import com.ts.server.ods.controller.form.ManagerInfoForm;
import com.ts.server.ods.controller.form.MemberInfoForm;
import com.ts.server.ods.controller.form.PasswordUpdateForm;
import com.ts.server.ods.controller.vo.MainDateVo;
import com.ts.server.ods.controller.vo.MainStatsVo;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.service.TaskCardService;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.domain.EvaluationLog;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 基础API接口，基础API接口需要授权
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/")
@Api(value = "/", tags = "基础API接口")
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    private final ManagerService managerService;
    private final MemberService memberService;
    private final ResourceService resourceService;
    private final EvaluationService evaluationService;
    private final TaskCardService taskCardService;

    @Autowired
    public BaseController(ManagerService managerService, MemberService memberService,
                          ResourceService resourceService, EvaluationService evaluationService,
                          TaskCardService taskCardService) {

        this.managerService = managerService;
        this.memberService = memberService;
        this.resourceService = resourceService;
        this.evaluationService = evaluationService;
        this.taskCardService = taskCardService;
    }

    @PostMapping(value = "/manage/updatePassword", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改申报员密码")
    public ResultVo<OkVo> manageUpdatePassword(@Validated @RequestBody PasswordUpdateForm form){
        return ResultVo.success(new OkVo(managerService.updatePassword(getCredential().getId(), form.getPassword(), form.getNewPassword())));
    }

    @PutMapping(value = "/manage/info", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改管理员信息")
    public ResultVo<Manager> manageUpdateInfo(@Validated @RequestBody ManagerInfoForm form){
        Manager m = managerService.get(getCredential().getId());
        m.setName(form.getName());
        m.setPhone(form.getPhone());

        return ResultVo.success(managerService.update(m));
    }

    @PostMapping(value = "/client/updatePassword", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改申报员密码")
    public ResultVo<OkVo> memberUpdatePassword(@Validated @RequestBody PasswordUpdateForm form){
        return ResultVo.success(new OkVo(memberService.updatePassword(getCredential().getId(), form.getPassword(), form.getNewPassword())));
    }

    @PutMapping(value = "/client/info", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改申报员信息")
    public ResultVo<Member> clientUpdateInfo(@Validated @RequestBody MemberInfoForm form){
        Member m = memberService.get(getCredential().getId());
        m.setName(form.getName());

        return ResultVo.success(memberService.update(m));
    }

    @GetMapping(value = "/download/{id}")
    public void download(@PathVariable("id")String id, HttpServletResponse response){
        try{
            Optional<Resource> optional = resourceService.get(id);
            if(!optional.isPresent()){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                LOGGER.error("Download resource fail id={}", id);
                return;
            }

            Resource t = optional.get();
            response.setHeader("Content-Disposition", "attachment; filename*=" + buildFilename(t.getFileName()));
            response.setContentType(t.getContentType());
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(t.getFileSize()));
            InputStream in= new FileInputStream(t.getPath());
            byte[] buf = new byte[4096];
            int len;
            while((len = in.read(buf)) != -1){
                response.getOutputStream().write(buf, 0, len);
            }
            response.flushBuffer();
        }catch (IOException e){
            LOGGER.debug("Download resource fail id={}, throw={}", id, e.getMessage());
            response.setStatus(HttpStatus.NOT_FOUND.value());
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

    @GetMapping(value = "/view/{id}")
    public void view(@PathVariable("id")String id, HttpServletResponse response){
        try{
            Optional<Resource> optional = resourceService.get(id);
            if(!optional.isPresent()){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                LOGGER.error("Download resource fail id={}", id);
                return;
            }

            Resource t = optional.get();

            if(StringUtils.isNotBlank(t.getViewUrl())){
                response.setStatus(HttpStatus.FOUND.value());
                response.setHeader(HttpHeaders.LOCATION, t.getViewUrl());
                response.flushBuffer();
                return;
            }

            response.setContentType(t.getContentType());
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(t.getFileSize()));
            InputStream in= new FileInputStream(t.getPath());
            byte[] buf = new byte[4096];
            int len;
            while((len = in.read(buf)) != -1){
                response.getOutputStream().write(buf, 0, len);
            }
            response.flushBuffer();
        }catch (IOException e){
            LOGGER.debug("Download resource fail id={}, throw={}", id, e.getMessage());
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
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
