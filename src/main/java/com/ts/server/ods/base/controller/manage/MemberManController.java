package com.ts.server.ods.base.controller.manage;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.controller.manage.form.MemberSaveForm;
import com.ts.server.ods.base.controller.manage.form.MemberUpdateForm;
import com.ts.server.ods.controller.form.PasswordResetForm;
import com.ts.server.ods.base.controller.manage.logger.MemberLogDetailBuilder;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.CompanyService;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 管理申报员API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/member")
@ApiACL({"ROLE_SYS"})
@Api(value = "/manage/member", tags = "管理申报员API接口")
public class MemberManController {
    private final MemberService service;
    private final CompanyService companyService;

    @Autowired
    public MemberManController(MemberService service, CompanyService companyService) {
        this.service = service;
        this.companyService = companyService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "添加申报员", buildDetail = MemberLogDetailBuilder.SaveBuilder.class)
    @ApiOperation("添加申报员")
    public ResultVo<Member> save(@Valid @RequestBody MemberSaveForm form){
        if(companyService.has(form.getCompanyId())){
            throw new BaseException("单位不存在");
        }
        Member member = service.save(form.toDomain());
        return ResultVo.success(member);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改申报员", buildDetail = MemberLogDetailBuilder.UpdateBuilder.class)
    @ApiOperation("修改申报员")
    public ResultVo<Member> update(@Valid @RequestBody MemberUpdateForm form){
        Member member = service.update(form.toDomain());
        return ResultVo.success(member);
    }

    @PutMapping(value = "activeManage/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "设置申报员为单位管理员", buildDetail = MemberLogDetailBuilder.ActiveManagerBuilder.class)
    @ApiOperation("设置申报员为单位管理员")
    public ResultVo<Member> activeManage(@PathVariable("id") String id){
        return ResultVo.success(service.activeManage(id));
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "删除申报员", buildDetail = MemberLogDetailBuilder.DeleteBuilder.class)
    @ApiOperation("删除申报员")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        boolean ok = service.delete(id);
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到申报人员")
    public ResultVo<Member> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @PutMapping(value = "resetPassword", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "重置申报员密码", buildDetail = MemberLogDetailBuilder.ResetPasswordBuilder.class)
    @ApiOperation("重置申报员密码")
    public ResultVo<OkVo> resetPassword(@Valid @RequestBody PasswordResetForm form){
        boolean ok = service.resetPassword(form.getId(), form.getNewPassword());
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "company/{companyId}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询单位用户")
    public ResultVo<List<Member>> queryOfCompany(@PathVariable("companyId") String companyId){
        return ResultVo.success(service.queryByCompanyId(companyId));
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询管理员")
    public ResultPageVo<Member> query(
            @ApiParam(value = "公司编号") @RequestParam(required = false) String companyId,
            @ApiParam(value = "用户名") @RequestParam(required = false) String username,
            @ApiParam(value = "电话号码") @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        return new ResultPageVo.Builder<>(page, rows, service.query(companyId, username, phone, page * rows, rows))
                .count(isCount, () -> service.count(companyId, username, phone))
                .build();
    }

}
