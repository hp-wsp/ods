package com.ts.server.ods.common.word;

import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Word2007格式文档导出Html
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class Word2007ExportHtml implements WordExport {
    private static final Logger LOGGER = LoggerFactory.getLogger(Word2007ExportHtml.class);
    private static final byte[] HTML_HEAD = convertBytes("<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><body>");
    private static final byte[] HTML_END = convertBytes("</body></html>");

    private final String rootDir;
    private final String rootUrl;

    public Word2007ExportHtml(String rootDir, String rootUrl) {
        this.rootDir = rootDir;
        this.rootUrl = rootUrl;
    }

    @Override
    public String export(InputStream inputStream, String outDir)throws WordExportException {

        try {
            createDir(String.format("%s/%s", rootDir, outDir));

            XWPFDocument document = new XWPFDocument(inputStream);
            XHTMLOptions options = buildOptions(outDir);

            return exportHtml(document, options, outDir);

        }catch (WordExportException e){
            throw e;
        }catch (Exception e){
            LOGGER.error("Export word2003 html fail throw={}", e.getMessage());
            throw new WordExportException(503, "导出HTML失败");
        }
    }

    private void createDir(String path)throws WordExportException{
        try{
            File dir = new File(path);
            if(dir.exists()){
               FileUtils.deleteDirectory(dir);
            }
            FileUtils.forceMkdir(dir);
        }catch (IOException e){
            LOGGER.error("Create export dir fail throw={}", e.getMessage());
            throw new WordExportException(501, "Work2003文档导出成HTML失败");
        }
    }

    private XHTMLOptions buildOptions(String outDir){
        XHTMLOptions options = XHTMLOptions.create();
        ImageManager imageManager = new OdsImageManager(new File(rootDir), outDir+"/img", rootUrl);
        options.setImageManager(imageManager);
        options.setIgnoreStylesIfUnused(true);
        options.setFragment(true);
        options.setOmitHeaderFooterPages(false);
        return options;
    }

    private String exportHtml(XWPFDocument document, XHTMLOptions options, String outDir)throws WordExportException {
        String filename = "index.html";
        String path = String.format("%s/%s/%s", rootDir, outDir, filename);

        try(OutputStream out = new FileOutputStream(new File(path))){
            out.write(HTML_HEAD);
            ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
            XHTMLConverter.getInstance().convert(document, tmpOut, options);
            out.write(tmpOut.toByteArray());
            out.write(HTML_END);
            return filename;
        }catch (Exception e){
            LOGGER.error("Export word2007 html fail dir={}, throw={}", outDir, e.getMessage());
            throw new WordExportException(503, "导出Html失败");
        }
    }

    private static byte[] convertBytes(String v){
        return v.getBytes(StandardCharsets.UTF_8);
    }
}
