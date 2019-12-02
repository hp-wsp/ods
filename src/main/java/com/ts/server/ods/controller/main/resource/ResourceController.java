package com.ts.server.ods.controller.main.resource;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Resource;
import com.ts.server.ods.base.service.ResourceService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * 资源API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/resource")
@ApiACL
@Api(value = "/resource", tags = "资源API接口")
public class ResourceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping(value = "/download/{id}")
    public ResponseEntity<FileSystemResource> download(@PathVariable("id")String id){

        //TODO 实现断线重连功能

        Optional<Resource> optional = resourceService.get(id);
        if(!optional.isPresent()){
            LOGGER.error("Download resource fail id={}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource t = optional.get();
        if(StringUtils.equals(t.getType(), "evaluation")){
            String url = "";
            try{
                 url = "/downland/" + t.getId() + "/"+ URLEncoder.encode(t.getFileName(), "UTF-8");
                LOGGER.debug("Evaluation downland url={}", url);
            }catch (Exception e){
                //none instance;
            }

            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .header(HttpHeaders.LOCATION, url)
                    .build();
        }

        try{
            File file = new File(t.getPath());
            LOGGER.debug("Downland file id={}, path={}, size={}", id, t.getPath(), file.length());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename*=" + buildFilename(t.getFileName()))
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/force-download"))
                    .body(new FileSystemResource(file));
        }catch (Exception e){
            LOGGER.debug("Downland file id={}, throw={}", id, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private String buildFilename(String filename){
        try{
            final String charset = "UTF-8";
            return charset + "''"+ URLEncoder.encode(filename, charset);
        }catch (UnsupportedEncodingException e){
            return "";
        }
    }

    @GetMapping(value = "/view/{id}")
    public void view(@PathVariable("id")String id, HttpServletResponse response){
        try{
            Optional<Resource> optional = resourceService.get(id);
            if(!optional.isPresent()){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                LOGGER.error("Download resource fail id={}", id);
                return;
            }

            Resource t = optional.get();

            if(StringUtils.isNotBlank(t.getViewUrl())){
                response.setStatus(HttpStatus.FOUND.value());
                response.setHeader(HttpHeaders.LOCATION, t.getViewUrl());
                response.flushBuffer();
                return;
            }

            response.setContentType(t.getContentType());
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(t.getFileSize()));
            InputStream in= new FileInputStream(t.getPath());
            byte[] buf = new byte[4096];
            int len;
            while((len = in.read(buf)) != -1){
                response.getOutputStream().write(buf, 0, len);
            }
            response.flushBuffer();
        }catch (IOException e){
            LOGGER.debug("Download resource fail id={}, throw={}", id, e.getMessage());
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }

    private Credential getCredential(){
        return CredentialContextUtils.getCredential().orElseThrow(() -> new BaseException("用户未授权"));
    }
}
