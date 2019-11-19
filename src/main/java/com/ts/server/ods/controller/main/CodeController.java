package com.ts.server.ods.controller.main;

import com.ts.server.ods.controller.main.vo.CodeVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.security.kaptcha.KaptchaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 验证码API
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/")
@Api(value = "/", tags = "验证码API接口")
public class CodeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeController.class);

    private final KaptchaService kaptchaService;

    @Autowired
    public CodeController(KaptchaService kaptchaService) {
        this.kaptchaService = kaptchaService;
    }

    @RequestMapping(value = "codeKey", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建验证码key")
    public ResultVo<CodeVo> codeKey(){
        return ResultVo.success(new CodeVo(StringUtils.remove(UUID.randomUUID().toString(), "-")));
    }

    @GetMapping(value = "codeImage")
    @ApiOperation("得到验证码")
    public void codeImage(@RequestParam("codeKey")String codeKey, HttpServletResponse response) {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        try(OutputStream outputStream = response.getOutputStream()){
            kaptchaService.writCodeImage(codeKey, outputStream);
        }catch (IOException e){
            LOGGER.error("Create cod fail error={}", e.getMessage());
            response.setStatus(400);
        }
    }
}
