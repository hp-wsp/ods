package com.ts.server.ods.etask.service;

import com.ts.server.ods.OdsProperties;
import com.ts.server.ods.common.word.WordExport;
import com.ts.server.ods.common.word.WordExportHtmlFactory;
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
    private final WordExportHtmlFactory factory;

    ExportWordToHtml(OdsProperties properties){
        this.properties = properties;
        this.factory = new WordExportHtmlFactory(properties.getViewDir(), properties.getViewUrl());
    }

    Optional<String> export(MultipartFile file, String id){
        Optional<WordExport> optional = factory.getContextType(file.getContentType());
        return optional.flatMap(e -> {
            try(InputStream in = file.getInputStream()){
                String n = optional.get().export(in, id);
                return Optional.of(String.format("%s/%s/%s", properties.getViewUrl(), id, n));
            }catch(Exception ex){
                LOGGER.error("Export word html fail id={},throws={}", id, ex.getMessage());
                return Optional.empty();
            }
        });
    }
}
