package com.ts.server.ods.etask.service;

import com.ts.server.ods.OdsProperties;
import com.ts.server.ods.common.word.Word2003ExportHtml;
import com.ts.server.ods.common.word.Word2007ExportHtml;
import com.ts.server.ods.common.word.WordExport;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

/**
 * 导出Word到html网页
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
class ExportWordToHtml {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportWordToHtml.class);

    private final OdsProperties properties;
    private final WordExport export2003;
    private final WordExport export2007;

    ExportWordToHtml(OdsProperties properties){
        this.properties = properties;
        this.export2003 = new Word2003ExportHtml(properties.getViewDir(), properties.getViewUrl());
        this.export2007 = new Word2007ExportHtml(properties.getViewDir(), properties.getViewUrl());
    }

    Optional<String> export(MultipartFile file, String id){
        Optional<WordExport> optional = getExportOpt(file);
        if(optional.isPresent()){
            LOGGER.debug("-----------------22------------------");
            try(InputStream in = file.getInputStream()){
                LOGGER.debug("-----------------33------------------");
                String n = optional.get().export(in, id);
                LOGGER.debug("-----------------44------------------");
                return Optional.of(String.format("%s/%s/%s", properties.getViewUrl(), id, n));
            }catch(Exception ex){
                LOGGER.error("Export word html fail id={},throws={}", id, ex.getMessage());
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private Optional<WordExport> getExportOpt(MultipartFile file){
        LOGGER.debug("-----------------1------------------");
        if(is2003(file)){
            LOGGER.debug("-----------------12------------------");
            return Optional.of(export2003);
        }
        if(is2007(file)){
            LOGGER.debug("-----------------21------------------");
            return Optional.of(export2007);
        }

        return Optional.empty();
    }

    private boolean is2007(MultipartFile file){
        String filename = file.getOriginalFilename();
        String ext = FilenameUtils.getExtension(filename);
        return StringUtils.equals(ext, "docx") ||
                StringUtils.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    private boolean is2003(MultipartFile file){
        String filename = file.getOriginalFilename();
        String ext = FilenameUtils.getExtension(filename);
        return StringUtils.equals(ext, "doc") ||
                StringUtils.equals(file.getContentType(), "application/msword");
    }
}
