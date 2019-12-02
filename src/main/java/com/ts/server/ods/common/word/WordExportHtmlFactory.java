package com.ts.server.ods.common.word;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * {@link WordExport} factory
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class WordExportHtmlFactory {
    private final WordExport word2003Export;
    private final WordExport word2007Export;

    /**
     * 构造{@link WordExportHtmlFactory}
     *
     * @param rootDir 保存根目录
     * @param rootUrl 访问基础路径
     */
    public WordExportHtmlFactory(String rootDir, String rootUrl) {
        this.word2003Export = new Word2003ExportHtml(rootDir, rootUrl);
        this.word2007Export = new Word2007ExportHtml(rootDir, rootUrl);
    }

    /**
     * 导出2003格式word
     *
     * @return {@link WordExport}
     */
    public WordExport word2003(){
        return word2003Export;
    }

    /**
     * 导出2007格式word
     *
     * @return {@link WordExport}
     */
    public WordExport word2007(){
        return word2007Export;
    }

    /**
     * 通过{@code contextType}获取{@link WordExport}
     *
     * @param contextType Http ContextType
     * @return {@link WordExport}
     */
    public Optional<WordExport> getContextType(String contextType){
        if(is2003(contextType)){
            return Optional.of(word2003Export);
        }
        if(is2007(contextType)){
            return Optional.of(word2007Export);
        }
        return Optional.empty();
    }

    private boolean is2007(String contextType){
        return StringUtils.equals(contextType, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    private boolean is2003(String contextType){
        return StringUtils.equals(contextType, "application/msword");
    }
}
