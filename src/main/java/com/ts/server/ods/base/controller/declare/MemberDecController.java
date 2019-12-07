package com.ts.server.ods.base.controller.declare;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.controller.declare.form.MemberSaveForm;
import com.ts.server.ods.base.controller.declare.form.MemberUpdateForm;
import com.ts.server.ods.base.controller.declare.logger.MemberDecLogDetailBuilder;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.form.PasswordResetForm;
import com.ts.server.ods.controller.main.declare.credential.DecCredential;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.CredentialContextUtils;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 管理本单位申报员API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/declare/member")
@ApiACL({"ROLE_DEC_MANAGE"})
@Api(value = "/declare/member", tags = "管理本单位申报员API接口")
public class MemberDecController {
    private final MemberService service;

    @Autowired
    public MemberDecController(MemberService service) {
        this.service = service;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "添加申报员", buildDetail = MemberDecLogDetailBuilder.SaveBuilder.class)
    @ApiOperation("添加申报员")
    public ResultVo<Member> save(@Valid @RequestBody MemberSaveForm form){
        DecCredential credential = getCredential();
        Member member = form.toDomain();
        member.setCompanyId(credential.getCompanyId());
        return ResultVo.success(service.save(member));
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改申报员", buildDetail = MemberDecLogDetailBuilder.UpdateBuilder.class)
    @ApiOperation("修改申报员")
    public ResultVo<Member> update(@Valid @RequestBody MemberUpdateForm form){
        validateCompanyMember(form.getId());

        Member member = service.update(form.toDomain());
        return ResultVo.success(member);
    }

    /**
     * 验证是本单位申报员
     *
     * @param id 申报员编号
     */
    private void validateCompanyMember(String id){
        DecCredential credential = getCredential();
        Member member = service.get(id);
        if(!StringUtils.equals(credential.getCompanyId(), member.getCompanyId())){
            throw new BaseException("非本单位申报员");
        }
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "删除申报员", buildDetail = MemberDecLogDetailBuilder.DeleteBuilder.class)
    @ApiOperation("删除申报员")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        validateCompanyMember(id);
        boolean ok = service.delete(id);
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到申报人员")
    public ResultVo<Member> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @PutMapping(value = "resetPassword", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "重置申报员密码", buildDetail = MemberDecLogDetailBuilder.ResetPasswordBuilder.class)
    @ApiOperation("重置申报员密码")
    public ResultVo<OkVo> resetPassword(@Valid @RequestBody PasswordResetForm form){
        validateCompanyMember(form.getId());
        boolean ok = service.resetPassword(form.getId(), form.getNewPassword());
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询管理员")
    public ResultPageVo<Member> query(
            @ApiParam(value = "用户名") @RequestParam(required = false) String username,
            @ApiParam(value = "电话号码") @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        String companyId =getCredential().getCompanyId();
        return new ResultPageVo.Builder<>(page, rows, service.query(companyId, username, phone, page * rows, rows))
                .count(isCount, () -> service.count(companyId, username, phone))
                .build();
    }

    private DecCredential getCredential(){
        return (DecCredential) CredentialContextUtils.getCredential()
                .orElseThrow(() -> new BaseException("用户未授权"));
    }
}
