package com.ts.server.ods.base.controller;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.controller.form.ManagerSaveForm;
import com.ts.server.ods.base.controller.form.ManagerUpdateForm;
import com.ts.server.ods.base.controller.form.PasswordResetForm;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.base.service.ManagerService;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/manage/manager")
@Api(value = "/manage/manager", tags = "管理员管理API接口")
public class ManagerContoller {

    private final ManagerService service;
    private final OptLogService optLogService;

    @Autowired
    public ManagerContoller(ManagerService service, OptLogService optLogService) {
        this.service = service;
        this.optLogService = optLogService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新增管理员")
    public ResultVo<Manager> save(@Valid @RequestBody ManagerSaveForm form){
        Manager manager = service.save(form.toDomain());

        optLogService.save("新增管理员", new String[]{"编号", "用户名"},
                new String[]{manager.getId(), manager.getName()}, getCredential().getUsername());

        return ResultVo.success(manager);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改管理员信息")
    public ResultVo<Manager> update(@Valid @RequestBody ManagerUpdateForm form){
        Manager manager = service.update(form.toDomain());

        optLogService.save("修改管理员", new String[]{"编号", "用户名"},
                new String[]{manager.getId(), manager.getName()}, getCredential().getUsername());

        return ResultVo.success(manager);
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("删除管理员")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        Manager manager = service.get(id);

        boolean ok = service.delete(id);
        if(ok){
            optLogService.save("删除管理员", new String[]{"编号", "用户名"},
                    new String[]{manager.getId(), manager.getName()}, getCredential().getUsername());
        }

        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到管理员信息")
    public ResultVo<Manager> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @PutMapping(value = "resetPassword", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("重置密码")
    public ResultVo<OkVo> resetPassword(@Valid @RequestBody PasswordResetForm form){
        Manager manager = service.get(form.getId());

        boolean ok = service.resetPassword(form.getId(), form.getNewPassword());
        if(ok){
            optLogService.save("重置管理员密码", new String[]{"编号", "用户名"},
                    new String[]{manager.getId(), manager.getName()}, getCredential().getUsername());
        }

        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询管理员")
    public ResultPageVo<Manager> query(
            @ApiParam(value = "用户名") @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        return new ResultPageVo.Builder<>(page, rows, service.query( username, page * rows, rows))
                .count(isCount, () -> service.count( username))
                .build();
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}

