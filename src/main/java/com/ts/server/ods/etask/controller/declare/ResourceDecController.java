package com.ts.server.ods.etask.controller.declare;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.controller.vo.OkVo;
import com.ts.server.ods.controller.vo.ResultVo;
import com.ts.server.ods.etask.controller.declare.logger.ResourceDecLogDetailBuilder;
import com.ts.server.ods.etask.domain.Declaration;
import com.ts.server.ods.etask.service.DeclarationService;
import com.ts.server.ods.logger.aop.annotation.EnableApiLogger;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * 申报资源API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/declare/resource")
@Api(value = "/declare/resource", tags = "申报资源API接口")
public class ResourceDecController {
    private final DeclarationService decService;

    @Autowired
    public ResourceDecController(DeclarationService decService) {
        this.decService = decService;
    }

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "新增申报资源", buildDetail = ResourceDecLogDetailBuilder.SaveBuilder.class)
    @ApiOperation("新增申报资源")
    public ResultVo<Declaration> save(@RequestParam(value = "file") @ApiParam(value = "上传文件", required = true) MultipartFile file,
                                      @RequestParam(value = "taskItemId") @ApiParam(value = "任务指标编号") String taskItemId){

        Credential credential = getCredential();

        String filename = file.getOriginalFilename();

        if(StringUtils.isBlank(filename)){
            throw new BaseException("文件名不能为空");
        }

        if(StringUtils.length(filename) > 64){
            throw new BaseException("文件名不能超过64个字符");
        }

        Declaration t = decService.save(taskItemId, credential.getId(), file);

        return ResultVo.success(t);
    }

    @DeleteMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @EnableApiLogger(name = "删除申报资源", buildDetail = ResourceDecLogDetailBuilder.DeleteBuilder.class)
    @ApiOperation("删除申报资源")
    public ResultVo<OkVo> delete(@ApiParam("申报资源编号") @PathVariable("id")String id){
        boolean ok = decService.delete(id, getCredential().getId());
        return ResultVo.success(new OkVo(ok));
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
