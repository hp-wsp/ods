package com.ts.server.ods.common.word;

import java.io.InputStream;

/**
 * Word单位导出接口
 *
 * @author <a href="mail:hhywangwei@gmail.com">WangWei</a>
 */
public interface WordExport {

    /**
     * 导出Word
     *
     * @param inputStream 输入流
     * @param path 保存HTML目录
     * @return 导出文件名
     * @throws WordExportException 异常类
     */
    String export(InputStream inputStream, String path)throws WordExportException;

}
