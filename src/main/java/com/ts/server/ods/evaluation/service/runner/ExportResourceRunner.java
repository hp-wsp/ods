package com.ts.server.ods.evaluation.service.runner;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.OdsProperties;
import com.ts.server.ods.base.domain.Resource;
import com.ts.server.ods.base.service.ResourceService;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.etask.domain.Declaration;
import com.ts.server.ods.etask.service.DeclarationService;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaItemService;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.exec.ProgressRunnable;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 导出测评资源
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ExportResourceRunner implements ProgressRunnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportResourceRunner.class);
    private static final int BATCH_SIZE = 10;

    private final EvaluationService evaluationService;
    private final EvaItemService itemService;
    private final DeclarationService declarationService;
    private final ResourceService resourceService;
    private final OdsProperties properties;
    private final String evaId;
    private final String password;

    private volatile int progress;

    public ExportResourceRunner(EvaluationService evaluationService, EvaItemService evaItemService,
                                DeclarationService declarationService, ResourceService resourceService,
                                OdsProperties properties, String evaId, String password) {

        this.evaluationService = evaluationService;
        this.itemService = evaItemService;
        this.declarationService = declarationService;
        this.resourceService = resourceService;
        this.properties = properties;
        this.evaId = evaId;
        this.password = password;
    }

    @Override
    public void run() {
        Evaluation evaluation = evaluationService.get(evaId);
        String taskId = IdGenerators.uuid();
        LOGGER.debug("Create exec task id evaId={}, taskId={}", evaId, taskId);
        try{
            String zipPath = archived(evaluation, taskId);
            evaluationService.updateExport(evaluation.getId(), taskId);
            saveResource(taskId, zipPath, String.format("%s.zip", evaluation.getName()));
            progress = 100;

        }catch (IOException e) {
            LOGGER.error("Export evaluation fail evaId={}, taskId={}, throw={}", evaId, taskId, e.getMessage());
            throw new BaseException(1101, "操作文件失败");
        }
    }

    private String archived(Evaluation evaluation, String taskId)throws IOException{
        long startTime = System.currentTimeMillis();
        LOGGER.debug("Start updateExport archived infoId={}, startTime={}", taskId, startTime);

        String zipPath = buildZipPath(taskId, evaluation.getName());

        try(FileOutputStream fOut = new FileOutputStream(new File(zipPath));
            ZipArchiveOutputStream tOut = new ZipArchiveOutputStream(fOut)) {

            tOut.setEncoding("UTF-8");
            tOut.setUseZip64(Zip64Mode.Always);

            int count = itemService.count(evaluation.getId(), "", "").intValue();
            int bCount = (count + BATCH_SIZE -1) / BATCH_SIZE;

            int finishCount = 0;
            Set<String> filenames = new HashSet<>(count);
            for(int i = 0 ; i < bCount; i++){
                List<EvaItem> items = itemService.query(evaluation.getId(),
                        "", "", i * BATCH_SIZE, BATCH_SIZE);

                for(EvaItem item: items){
                    zip(filenames, tOut, item);
                    finishCount++;
                    progress =  finishCount * 100 / count;
                }
            }
        }catch (IOException e){
            LOGGER.debug("Archived resource taskId={}, throw={}", taskId, e.getMessage());
        }

        long useTime = (System.currentTimeMillis() - startTime)/1000;
        LOGGER.debug("Export archived success infoId={},useSeconds={}", taskId, useTime);
        return zipPath;
    }

    private String buildZipPath(String id, String filename)throws IOException {
        String dirPath = properties.getResource() + "/target/" + id;
        File dir = new File(dirPath);
        FileUtils.forceMkdir(dir);
        return dirPath + "/" + filename + ".zip";
    }

    private void zip(Set<String> filenames, ZipArchiveOutputStream zOut, EvaItem item)throws IOException{
        List<Declaration> lst = declarationService.queryByEvaItemId(item.getId());
        for(Declaration t: lst){
            String filename= targetFile(filenames, item, t);
            LOGGER.debug("Get declaration resources itemId={}, path={}, filename={},",
                    item.getId(), t.getPath(), filename);
            addFileToZip(zOut, t.getPath(), filename);
        }
    }

    private String targetFile(Set<String> filenames, EvaItem item, Declaration t){
        String filename = buildZipFilename(item.getNum(), t.getFileName());
        if(!filenames.contains(filename)){
            filenames.add(filename);
            return filename;
        }

        String baseName = FilenameUtils.getBaseName(t.getFileName());
        String extName = FilenameUtils.getExtension(t.getFileName());
        for(int i = 1; i < 100; i++){
            String newFilename = String.format("%s%02d.%s", baseName, i, extName);
            String path = buildZipFilename(item.getNum(), newFilename);
            if(!filenames.contains(path)){
                filenames.add(path);
                return path;
            }
        }

        throw new BaseException("同名文件超过100个导出失败");
    }

    private String buildZipFilename(String num, String filename){
        return String.format("%s/%s",  num, filename);
    }

    private static void addFileToZip(ZipArchiveOutputStream zOut, String path, String entryName) throws IOException {
        File f = new File(path);

        try (FileInputStream fInputStream = new FileInputStream(f)){
            ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
            entry.setSize(f.length());
            entry.setTime(System.currentTimeMillis());
            zOut.putArchiveEntry(entry);
            IOUtils.copy(fInputStream, zOut);
            zOut.closeArchiveEntry();
        }
    }

    private void saveResource(String id, String path, String filename){
        Resource t = new Resource();

        t.setId(id);
        t.setFileName(filename);
        t.setContentType("application/zip");
        t.setPath(path);
        File f = new File(path);
        t.setFileSize((int)f.length());
        t.setType("evaluation");

        resourceService.save(t);
    }

    @Override
    public int progress() {
        return this.progress;
    }
}
