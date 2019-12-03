package com.ts.server.ods.controller.main.resource;

import com.ts.server.ods.base.domain.Resource;
import com.ts.server.ods.base.service.ResourceService;
import com.ts.server.ods.common.utils.HttpUtils;
import com.ts.server.ods.security.annotation.ApiACL;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.*;

/**
 * 资源API接口
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@RestController
@RequestMapping("/resource")
//@ApiACL
@Api(value = "/resource", tags = "资源API接口")
public class ResourceController {
    private static final long MAX_RAGE = (Integer.MAX_VALUE/2);
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping(value = "/download/{id}")
    public void download(@PathVariable(value = "id")String id,
                         @RequestHeader(value = HttpHeaders.RANGE, required = false) String range,
                         HttpServletResponse response){

        Optional<Resource> optional = resourceService.get(id);

        if(!optional.isPresent()){
            LOGGER.warn("Download resource not exist id={}", id);
            responseNotFound(response);
            return ;
        }

        Resource t = optional.get();
        File file = new File(t.getPath());
        if(!file.isFile()){
           LOGGER.warn("Download file not exist id={}", id);
           responseNotFound(response);
           return ;
        }

        RangeSetting rangeSetting = buildRangeSetting(file.length(), range);
        setDownlandHttpHeader(response, t.getFileName(), rangeSetting);
        LOGGER.debug("Downland file id={}, path={}, size={}", id, t.getPath(), file.length());

        try(OutputStream outputStream = response.getOutputStream();
            WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
            RandomAccessFile  randomAccessFile = new RandomAccessFile(file, "r")){
            long len = rangeSetting.totalLen - rangeSetting.start;
            int c = (int)((len + MAX_RAGE) / MAX_RAGE);
            for(int i = 0; i < c; i++){
                long start = rangeSetting.start + i * MAX_RAGE;
                long remain = rangeSetting.totalLen - start;
                long contentLen = Math.min(remain, MAX_RAGE);
                randomAccessFile.getChannel().transferTo(start, contentLen, writableByteChannel);
            }
        }catch (IOException e){
            LOGGER.debug("Download resource fail id={}, throw={}", id, e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * 输出资源部存在
     *
     * @param response {@link HttpServletResponse}
     */
    private void responseNotFound(HttpServletResponse response){
        response.setStatus(HttpStatus.NOT_FOUND.value());
    }

    /**
     * 构建{@link RangeSetting}
     *
     * @param totalLen 下载文件总长度
     * @param range http header中Range
     * @return {@link RangeSetting}
     */
    private RangeSetting buildRangeSetting(long totalLen, String range){
        return new RangeSetting.Builder(totalLen, range).build();
    }

    /**
     * 设置下载头
     *
     * @param response {@link HttpServletResponse}
     * @param filename 下载文件名
     * @param rangeSetting {@link RangeSetting}
     */
    private void setDownlandHttpHeader(HttpServletResponse response, String filename, RangeSetting rangeSetting){
        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
        response.setContentType("application/force-download");
        HttpUtils.setContentDisposition(response, filename);
        String contentRange = "bytes " + rangeSetting.start + "-" + rangeSetting.end + "/" + rangeSetting.totalLen;
        response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
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

    /**
     * 下载Range
     */
    static class RangeSetting {
        private static final String RANGE_SEPARATOR = "-";

        static class Builder {
            private final long totalLen;
            private final String range;

            Builder(long totalLen, String range) {
                this.totalLen = totalLen;
                this.range = range;
            }

            public RangeSetting build(){
                if(StringUtils.isBlank(range) || !StringUtils.contains(range, '-')){
                    return new RangeSetting(0, totalLen -1, totalLen);
                }

                if (StringUtils.startsWith(range,RANGE_SEPARATOR)) {
                    long contentLen = Long.parseLong(StringUtils.substring(range, 1));
                    return new RangeSetting(totalLen - contentLen, totalLen -1,  totalLen);
                }

                if (StringUtils.endsWith(range,RANGE_SEPARATOR)){
                    long  start = Long.parseLong(StringUtils.left(range, range.length() -1));
                    return new RangeSetting(start, totalLen -1, totalLen);
                }

                String[] se = StringUtils.split(range, RANGE_SEPARATOR);
                long start = Long.parseLong(se[0]);
                long end = Long.parseLong(se[1]);
                return new RangeSetting(start, end, totalLen);
            }
        }

        private final long start;
        private final long end;
        private final long totalLen;

        RangeSetting(long start, long end, long totalLen) {
            this.start = start;
            this.end = end;
            this.totalLen = totalLen;
        }
    }
}
