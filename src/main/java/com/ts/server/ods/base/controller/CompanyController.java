package com.ts.server.ods.base.controller;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.controller.form.CompanySaveForm;
import com.ts.server.ods.base.controller.form.CompanyUpdateForm;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.service.CompanyService;
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
 * 公司管理API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/company")
@Api(value = "/manage/company", tags = "公司管理API接口")
public class CompanyController {

    private final CompanyService service;
    private final OptLogService optLogService;

    @Autowired
    public CompanyController(CompanyService service, OptLogService optLogService) {
        this.service = service;
        this.optLogService = optLogService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新增公司")
    public ResultVo<Company> save(@Valid @RequestBody CompanySaveForm form){
        Company company = service.save(form.toDomain());

        optLogService.save("新增公司", new String[]{"编号", "名称"},
                new String[]{company.getId(), company.getName()}, getCredential().getUsername());

        return ResultVo.success(company);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改公司")
    public ResultVo<Company> update(@Valid @RequestBody CompanyUpdateForm form){
        Company company = service.update(form.toDomain());

        optLogService.save("修改公司", new String[]{"编号", "名称"},
                new String[]{company.getId(), company.getName()}, getCredential().getUsername());

        return ResultVo.success(company);
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("删除公司")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        Company company = service.get(id);

        boolean ok = service.delete(id);

        if(ok){
            optLogService.save("删除公司", new String[]{"编号", "名称"},
                    new String[]{company.getId(), company.getName()}, getCredential().getUsername());
        }

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

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}