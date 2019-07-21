package com.ts.server.ods.base.controller;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.controller.form.MemberSaveForm;
import com.ts.server.ods.base.controller.form.MemberUpdateForm;
import com.ts.server.ods.base.controller.form.PasswordResetForm;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.logger.service.OptLogService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
/**
 * 申报人员API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/member")
@Api(value = "/manage/member", tags = "申报人员API接口")
public class MemberController {
    private final MemberService service;
    private final OptLogService optLogService;

    @Autowired
    public MemberController(MemberService service, OptLogService optLogService) {
        this.service = service;
        this.optLogService = optLogService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("添加申报人员")
    public ResultVo<Member> save(@Valid @RequestBody MemberSaveForm form){
        Member member = service.save(form.toDomain());

        optLogService.save("添加申报人员", new String[]{"编号", "用户名"},
                new String[]{member.getId(), member.getUsername()}, getCredential().getUsername());

        return ResultVo.success(member);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改申报人员")
    public ResultVo<Member> update(@Valid @RequestBody MemberUpdateForm form){
        Member member = service.update(form.toDomain());

        optLogService.save("修改申报人员", new String[]{"编号", "用户名"},
                new String[]{member.getId(), member.getUsername()}, getCredential().getUsername());

        return ResultVo.success(member);
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("删除申报人员")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        Member member = service.get(id);

        boolean ok = service.delete(id);
        if(ok){
            optLogService.save("删除申报人员", new String[]{"编号", "用户名"},
                    new String[]{member.getId(), member.getUsername()}, getCredential().getUsername());
        }
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到申报人员")
    public ResultVo<Member> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @PutMapping(value = "/resetPassword", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("重置密码")
    public ResultVo<OkVo> resetPassword(@Valid @RequestBody PasswordResetForm form){
        Member member = service.get(form.getId());

        boolean ok = service.resetPassword(form.getId(), form.getNewPassword());
        if(ok){
            optLogService.save("重置申报人员密码", new String[]{"编号", "用户名"},
                    new String[]{member.getId(), member.getUsername()}, getCredential().getUsername());
        }

        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "/company/{companyId}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询单位用户")
    public ResultVo<List<Member>> queryOfComapny(@PathVariable("companyId") String companyId){
        return ResultVo.success(service.queryByCompanyId(companyId));
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询管理员")
    public ResultPageVo<Member> query(
            @ApiParam(value = "公司编号") @RequestParam(required = false) String companyId,
            @ApiParam(value = "单位名称") @RequestParam(required = false) String companyName,
            @ApiParam(value = "用户名") @RequestParam(required = false) String username,
            @ApiParam(value = "电话号码") @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        return new ResultPageVo.Builder<>(page, rows, service.query(companyId, companyName, username, phone, page * rows, rows))
                .count(isCount, () -> service.count(companyId, companyName, username, phone))
                .build();
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
