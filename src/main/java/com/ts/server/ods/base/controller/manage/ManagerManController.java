package com.ts.server.ods.base.controller.manage;

import com.ts.server.ods.base.controller.manage.form.ManagerSaveForm;
import com.ts.server.ods.base.controller.manage.form.ManagerUpdateForm;
import com.ts.server.ods.controller.form.PasswordResetForm;
import com.ts.server.ods.base.controller.manage.logger.ManagerLogDetailBuilder;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.base.service.ManagerService;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * 管理员管理API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/manager")
@ApiACL({"ROLE_SYS"})
@Api(value = "/manage/manager", tags = "管理员管理API接口")
public class ManagerManController {

    private final ManagerService service;

    @Autowired
    public ManagerManController(ManagerService service) {
        this.service = service;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "新增管理员", buildDetail = ManagerLogDetailBuilder.SaveBuilder.class)
    @ApiOperation("新增管理员")
    public ResultVo<Manager> save(@Valid @RequestBody ManagerSaveForm form){
        Manager manager = service.save(form.toDomain());
        return ResultVo.success(manager);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改管理员信息", buildDetail = ManagerLogDetailBuilder.UpdateBuilder.class)
    @ApiOperation("修改管理员信息")
    public ResultVo<Manager> update(@Valid @RequestBody ManagerUpdateForm form){
        Manager manager = service.update(form.toDomain());
        return ResultVo.success(manager);
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "删除管理员", buildDetail = ManagerLogDetailBuilder.DeleteBuilder.class)
    @ApiOperation("删除管理员")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        boolean ok = service.delete(id);
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到管理员信息")
    public ResultVo<Manager> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @PutMapping(value = "resetPassword", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "重置管理员密码", buildDetail = ManagerLogDetailBuilder.ResetPasswordBuilder.class)
    @ApiOperation("重置管理员密码")
    public ResultVo<OkVo> resetPassword(@Valid @RequestBody PasswordResetForm form){
        boolean ok = service.resetPassword(form.getId(), form.getNewPassword());
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
}

