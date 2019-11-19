package com.ts.server.ods.controller.main.mangae;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.SmsProperties;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.base.service.ManagerService;
import com.ts.server.ods.common.utils.HttpUtils;
import com.ts.server.ods.controller.main.form.LoginForm;
import com.ts.server.ods.controller.main.vo.LoginVo;
import com.ts.server.ods.controller.vo.*;
import com.ts.server.ods.security.kaptcha.KaptchaService;
import com.ts.server.ods.logger.service.OptLogService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.authenticate.GlobalRole;
import com.ts.server.ods.security.limit.LoginLimitService;
import com.ts.server.ods.security.token.TokenService;
import com.ts.server.ods.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 管理端通用API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage")
@Api(value = "/manage", tags = "管理端通用API接口")
public class MainManageController {

    private final ManagerService managerService;
    private final TokenService tokenService;
    private final OptLogService optLogService;
    private final SmsService smsService;
    private final SmsProperties properties;
    private final LoginLimitService loginLimitService;
    private final KaptchaService kaptchaService;

    @Autowired
    public MainManageController(ManagerService managerService, TokenService tokenService,
                                OptLogService optLogService, SmsService smsService, SmsProperties properties,
                                LoginLimitService loginLimitService, KaptchaService kaptchaService) {

        this.managerService = managerService;
        this.tokenService = tokenService;
        this.optLogService = optLogService;
        this.smsService = smsService;
        this.properties = properties;
        this.loginLimitService = loginLimitService;
        this.kaptchaService= kaptchaService;
    }

    @PostMapping(value = "login", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("管理员登录")
    public ResultVo<LoginVo<Manager>> login(@Valid @RequestBody LoginForm form, HttpServletRequest request){
        boolean needCode  = loginLimitService.getFail(form.getUsername()) > 3;

        if(needCode && StringUtils.isBlank(form.getCode())){
            throw new BaseException(103, "验证码不能为空");
        }
        if(needCode && !kaptchaService.validate(form.getCodeKey(),form.getCode())){
            throw new BaseException(104, "验证码错误");
        }

        Optional<Manager> optional = managerService.getValidate(form.getUsername(), form.getPassword());
        if(optional.isPresent()){
            loginLimitService.resetFail(form.getUsername());
        }else{
            if(loginLimitService.incFail(form.getUsername()) > 4){
                throw new BaseException(102, "用户或密码错误");
            }
            throw new BaseException(101, "用户名或密码错误");
        }

        Manager m = optional.get();
        Credential credential = new Credential(m.getId(), m.getUsername(),
                Arrays.asList(m.getRole(), GlobalRole.ROLE_AUTHENTICATION.name()));
        String token = tokenService.generate(credential);

        optLogService.save("管理员登录", new String[]{"用户名", "IP"},
                new Object[]{form.getUsername(), HttpUtils.getHostname(request)}, form.getUsername());

        return ResultVo.success(new LoginVo<>(token, m));
    }

    @GetMapping(value = "logout", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("管理员退出")
    public ResultVo<OkVo> logout(@RequestHeader(value = "token", required = false)String token,
                                 @RequestHeader(value = "Authorization", required = false) String auth){

        exit(token, auth);
        return ResultVo.success(new OkVo(true));
    }

    private void exit(String token, String auth){
        if(StringUtils.isBlank(token) && StringUtils.isBlank(auth)){
            return ;
        }

        if(StringUtils.isNotBlank(token)){
            tokenService.destroy(token);
            return ;
        }

        String t = StringUtils.trim(StringUtils.removeStart(auth, "Bearer"));
        tokenService.destroy(t);
    }

    @GetMapping(value = "smsPassword", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("通过短信获取管理员登录密码")
    public ResultVo<OkVo> smsPassword(@RequestParam String username){
        Optional<Manager> optional = managerService.getUsername(username);
        if(!optional.isPresent()){
            return ResultVo.error(456, "用户不存在");
        }

        Manager manager = optional.get();
        smsService.sendTemplate(manager.getPhone(), properties.getPasswordTmp(),
                new String[]{manager.getUsername(), manager.getPassword()},  e -> String.format("%s找回密码为*******", e[0]));

        return ResultVo.success(new OkVo(true));
    }
}
