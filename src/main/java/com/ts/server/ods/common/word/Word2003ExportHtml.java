package com.ts.server.ods.common.word;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.UUID;

/**
 * Word2003格式文档导出Html
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
class Word2003ExportHtml implements WordExport {
    private static final Logger LOGGER = LoggerFactory.getLogger(Word2003ExportHtml.class);

    private final String rootDir;
    private final String rootUrl;

    Word2003ExportHtml(String rootDir, String rootUrl) {
        this.rootDir = rootDir;
        this.rootUrl = rootUrl;
    }

    @Override
    public String export(InputStream inputStream, String outDir)throws WordExportException {

        try {
            createDir(String.format("%s/%s", rootDir, outDir));

            HWPFDocument doc = new HWPFDocument(inputStream);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            WordToHtmlConverter converter = new WordToHtmlConverter(document);
            setExportImage(converter, outDir);
            converter.processDocument(doc);
            return exportHtml(converter, outDir);
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

    private void setExportImage(WordToHtmlConverter converter, String outDir)throws WordExportException{
        try{
            converter.setPicturesManager((c, t, n, w, h) -> {
                LOGGER.debug("Export word image name={},type={},width={},height={}", n, t.getMime(), w, h);

                String filename = StringUtils.remove(UUID.randomUUID().toString(),  "-");
                String filePath =  String.format("%s/%s/%s.%s", rootDir, outDir, filename, t.getExtension());
                LOGGER.debug("Export image save path={}", filePath);

                try(OutputStream os = new FileOutputStream(filePath)){
                    os.write(c);
                }catch (IOException e){
                    LOGGER.error("Export image fail path={},throw={}", outDir, e.getMessage());
                }
                return String.format("%s/%s/%s.%s", rootUrl, outDir, filename, t.getExtension());
            });
        }catch (Exception e){
            LOGGER.error("Export image fail path={},throw={}", outDir, e.getMessage());
        }
    }

    private String exportHtml(WordToHtmlConverter converter, String outDir)throws WordExportException {
        Document htmlDocument = converter.getDocument();

        String filename = "index.html";
        File htmlFile = new File(String.format("%s/%s/%s", rootDir, outDir, filename));
        try(OutputStream out = new FileOutputStream(htmlFile)){
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer serializer = factory.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");

            serializer.transform(domSource, streamResult);

            return filename;
        }catch (Exception e){
            LOGGER.error("Export word2003 html fail dir={}, throw={}", outDir, e.getMessage());
            throw new WordExportException(503, "导出Html失败");
        }
    }
}
