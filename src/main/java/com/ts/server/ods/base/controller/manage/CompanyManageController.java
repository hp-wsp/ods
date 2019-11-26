package com.ts.server.ods.base.controller.manage;

import com.ts.server.ods.base.controller.manage.form.CompanySaveForm;
import com.ts.server.ods.base.controller.manage.form.CompanyUpdateForm;
import com.ts.server.ods.base.controller.manage.logger.CompanyLogDetailBuilder;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.service.CompanyService;
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
 * 单位管理API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/company")
@ApiACL({"ROLE_SYS"})
@Api(value = "/manage/company", tags = "单位管理API接口")
public class CompanyManageController {
    private final CompanyService service;

    @Autowired
    public CompanyManageController(CompanyService service) {
        this.service = service;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "新增单位", buildDetail = CompanyLogDetailBuilder.SaveBuilder.class)
    @ApiOperation("新增单位")
    public ResultVo<Company> save(@Valid @RequestBody CompanySaveForm form){
        Company company = service.save(form.toDomain());
        return ResultVo.success(company);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改单位", buildDetail = CompanyLogDetailBuilder.UpdateBuilder.class)
    @ApiOperation("修改单位")
    public ResultVo<Company> update(@Valid @RequestBody CompanyUpdateForm form){
        Company company = service.update(form.toDomain());
        return ResultVo.success(company);
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改单位", buildDetail = CompanyLogDetailBuilder.DeleteBuilder.class)
    @ApiOperation("删除公司")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        boolean ok = service.delete(id);
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到公司")
    public ResultVo<Company> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @GetMapping(value = "notAss")
    @ApiOperation("查询未分配任务单位")
    public ResultVo<List<Company>> queryNotAss(@RequestParam String evaId, @RequestParam(required = false)String name){
        return ResultVo.success(service.queryNotAss(evaId, name));
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询公司")
    public ResultPageVo<Company> query(
            @ApiParam(value = "公司名") @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        return new ResultPageVo.Builder<>(page, rows, service.query( name,page * rows, rows))
                .count(isCount, () -> service.count( name))
                .build();
    }
}