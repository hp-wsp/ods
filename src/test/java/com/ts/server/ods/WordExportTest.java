package com.ts.server.ods;

import com.ts.server.ods.common.word.Word2003ExportHtml;
import com.ts.server.ods.common.word.Word2007ExportHtml;
import com.ts.server.ods.common.word.WordExport;
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
        WordExport export = new Word2007ExportHtml("d:/data", "/test");
        try(InputStream inputStream = WordExportTest.class.getResourceAsStream("word2007.docx")){
            export.export(inputStream, "word2007");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
