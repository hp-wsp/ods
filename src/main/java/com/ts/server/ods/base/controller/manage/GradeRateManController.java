package com.ts.server.ods.base.controller.manage;

import com.ts.server.ods.base.controller.manage.form.GradeRateSaveForm;
import com.ts.server.ods.base.controller.manage.form.GradeRateUpdateForm;
import com.ts.server.ods.base.controller.manage.logger.GradeRateLogDetailBuilder;
import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.base.service.GradeRateService;
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
 * 得分比率设置API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/gradeRate")
@ApiACL({"ROLE_SYS"})
@Api(value = "/manage/gradeRate", tags = "得分比率设置API接口")
public class GradeRateManController {
    private final GradeRateService service;

    @Autowired
    public GradeRateManController(GradeRateService service) {
        this.service = service;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "新增得分比率", buildDetail = GradeRateLogDetailBuilder.SaveBuilder.class)
    @ApiOperation("新增得分比率")
    public ResultVo<GradeRate> save(@Valid @RequestBody GradeRateSaveForm form){
        return ResultVo.success(service.save(form.toDomain()));
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "修改得分比率", buildDetail = GradeRateLogDetailBuilder.UpdateBuilder.class)
    @ApiOperation("修改得分比率")
    public ResultVo<GradeRate> update(@Valid @RequestBody GradeRateUpdateForm form){
        return ResultVo.success(service.update(form.toDomain()));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到得分比率")
    public ResultVo<GradeRate> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "删除得分比率", buildDetail = GradeRateLogDetailBuilder.DeleteBuilder.class)
    @ApiOperation("删除得分比率")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        return ResultVo.success(new OkVo(service.delete(id)));
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询评分比率")
    public ResultPageVo<GradeRate> query(
            @ApiParam(value = "级别") @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        return new ResultPageVo.Builder<>(page, rows, service.query( level, page * rows, rows))
                .count(isCount, () -> service.count( level))
                .build();
    }
}
