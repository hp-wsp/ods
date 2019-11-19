package com.ts.server.ods.etask.controller.manage;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultPageVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.controller.manage.form.TaskCardSaveForm;
import com.ts.server.ods.etask.controller.manage.form.TaskCardUpdateForm;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.service.TaskCardService;
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
/**
 * 评测卡API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/manage/task/card")
@Api(value = "/manage/task/card", tags = "评测卡API接口")
public class TaskCardManageController {
    private final TaskCardService service;
    private final OptLogService optLogService;

    @Autowired
    public TaskCardManageController(TaskCardService service, OptLogService optLogService) {
        this.service = service;
        this.optLogService = optLogService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新增评测卡")
    public ResultVo<TaskCard> save(@Valid @RequestBody TaskCardSaveForm form){
        TaskCard card = service.save(form.toDomain());

        optLogService.save("新增评测卡", new String[]{"编号", "单位"},
                new String[]{card.getId(), card.getCompanyName()}, getCredential().getUsername());

        return ResultVo.success(card);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("修改评测卡")
    public ResultVo<TaskCard> update(@Valid @RequestBody TaskCardUpdateForm form){
        TaskCard card = service.update(form.toDomain());

        optLogService.save("修改评测卡", new String[]{"编号", "单位"},
                new String[]{card.getId(), card.getCompanyName()}, getCredential().getUsername());

        return ResultVo.success(card);
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("删除评测卡")
    public ResultVo<OkVo> delete(@PathVariable("id")String id){
        TaskCard card = service.get(id);

        boolean ok = service.delete(id);
        if(ok){
            optLogService.save("删除评测卡", new String[]{"编号", "单位"},
                    new String[]{card.getId(), card.getCompanyName()}, getCredential().getUsername());
        }
        return ResultVo.success(new OkVo(ok));
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("得到评测卡")
    public ResultVo<TaskCard> get(@PathVariable("id")String id){
        return ResultVo.success(service.get(id));
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("查询评比")
    public ResultPageVo<TaskCard> query(
            @ApiParam(value = "评测编号") @RequestParam(required = false) String evaId,
            @ApiParam(value = "单位名称") @RequestParam(required = false) String companyName,
            @ApiParam(value = "审核人员用户名") @RequestParam(required = false) String assUsername,
            @ApiParam(value = "申报人员用户名") @RequestParam(required = false) String decUsername,
            @RequestParam(defaultValue = "true") @ApiParam(value = "是否得到查询记录数") boolean isCount,
            @RequestParam(defaultValue = "0") @ApiParam(value = "查询页数") int page,
            @RequestParam(defaultValue = "15") @ApiParam(value = "查询每页记录数") int rows){

        return new ResultPageVo.Builder<>(page, rows, service.query( evaId, companyName, assUsername, decUsername,page * rows, rows))
                .count(isCount, () -> service.count(evaId, companyName, assUsername, decUsername))
                .build();
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
