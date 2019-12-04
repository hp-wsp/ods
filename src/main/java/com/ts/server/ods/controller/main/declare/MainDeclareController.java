package com.ts.server.ods.controller.main.declare;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.SmsProperties;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.main.form.LoginForm;
import com.ts.server.ods.controller.main.logger.LoginLogDetailBuilder;
import com.ts.server.ods.controller.main.logger.ObtainLoginUsername;
import com.ts.server.ods.controller.main.vo.LoginVo;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.authenticate.GlobalRole;
import com.ts.server.ods.security.kaptcha.KaptchaService;
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
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 申报端通用API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/declare/main")
@Api(value = "/declare/main", tags = "通用API接口")
public class MainDeclareController {

    private final MemberService memberService;
    private final TokenService tokenService;
    private final SmsService smsService;
    private final SmsProperties properties;
    private final LoginLimitService loginLimitService;
    private final KaptchaService kaptchaService;

    @Autowired
    public MainDeclareController(MemberService memberService, TokenService tokenService,
                                 SmsService smsService, SmsProperties properties,
                                LoginLimitService loginLimitService, KaptchaService kaptchaService) {

        this.memberService = memberService;
        this.tokenService = tokenService;
        this.smsService = smsService;
        this.properties = properties;
        this.loginLimitService = loginLimitService;
        this.kaptchaService= kaptchaService;
    }


    @PostMapping(value = "login", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "申报人员登录", buildDetail = LoginLogDetailBuilder.class, obtainUsername = ObtainLoginUsername.class)
    @ApiOperation("申报人员登录")
    public ResultVo<LoginVo<Member>> memberLogin(@Valid @RequestBody LoginForm form){

        //放置于管理员用户名冲突
        String username = String.format("DEC_%s", form.getUsername());

        //验证码验证
        if(loginLimitService.incFail(username) > 3){
            if(StringUtils.isBlank(form.getCode())){
                throw new BaseException(103, "验证码不能为空");
            }
            if(!kaptchaService.validate(form.getCodeKey(),form.getCode())){
                throw new BaseException(104, "验证码错误");
            }
        }

        Optional<Member> optional = memberService.getValidate(form.getUsername(), form.getPassword());
        if(!optional.isPresent()){
            int errCode = loginLimitService.getFail(username) >= 3? 102: 101;
            throw new BaseException(errCode, "用户名或密码错误");
        }

        loginLimitService.resetFail(username);
        Member m = optional.get();
        Credential credential = new Credential(m.getId(), m.getUsername(),
                Arrays.asList("ROLE_DECLARATION", GlobalRole.ROLE_AUTHENTICATION.name()));
        String token = tokenService.generate(credential);

        return ResultVo.success(new LoginVo<>(token, m));
    }

    @GetMapping(value = "logout", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("申报人员退出")
    public ResultVo<OkVo> memberLogout(@RequestHeader(value = "token", required = false)String token,
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
    @ApiOperation("通过短信获取申报员登录密码")
    public ResultVo<OkVo> memberSmsPassword(@RequestParam String username){
        Optional<Member> optional = memberService.getUsername(username);
        if(!optional.isPresent()){
            return ResultVo.error(456, "用户不存在");
        }

        Member member = optional.get();
        smsService.sendTemplate(member.getPhone(), properties.getPasswordTmp(),
                new String[]{member.getUsername(), member.getPassword()},  e -> String.format("%s找回密码为*******", e[0]));

        return ResultVo.success(new OkVo(true));
    }
}
