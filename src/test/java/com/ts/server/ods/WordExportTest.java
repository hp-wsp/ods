package com.ts.server.ods;

import com.ts.server.ods.common.word.WordExport;
import com.ts.server.ods.common.word.WordExportHtmlFactory;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * 导出Word单元测试
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class WordExportTest {

    @Test
    public void testWord2007ExportHtml(){
        WordExport export = new WordExportHtmlFactory("d:/data", "/test").word2007();
        try(InputStream inputStream = WordExportTest.class.getResourceAsStream("word2007.docx")){
            export.export(inputStream, "word2007");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
