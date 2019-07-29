package com.ts.server.ods.sms.controller;

import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.sms.domain.SmsLog;
import com.ts.server.ods.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 短信查询API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/sms")
@Api(value = "/manage/sms", tags = "短信发送查询API接口")
public class SmsController {
    private final SmsService service;

    @Autowired
    public SmsController(SmsService service) {
        this.service = service;
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询短信日志")
    public ResultPageVo<SmsLog> query(
            @ApiParam(value = "电话号码") @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        return new ResultPageVo.Builder<>(page, rows, service.query( phone, true,page * rows, rows))
                .count(isCount, () -> service.count(phone, true))
                .build();
    }
}
