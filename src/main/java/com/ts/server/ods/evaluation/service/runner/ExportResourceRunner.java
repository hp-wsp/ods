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
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 导出测评资源
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ExportResourceRunner implements ProgressRunnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportResourceRunner.class);
    private static final int BATCH_SIZE = 100;

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
            String arcDir = archived(evaluation, taskId);
            String zipPath = zip(taskId, arcDir, password);

            evaluationService.updateExport(evaluation.getId(), taskId);
            saveResource(taskId, zipPath, String.format("%s.zip", evaluation.getName()));
            progress = 100;

        }catch (IOException e) {
            LOGGER.error("Export evaluation fail evaId={}, taskId={}, throw={}", evaId, taskId, e.getMessage());
            throw new BaseException(1101, "操作文件失败");
        }catch (ZipException e){
            LOGGER.error("Zip evaluation fail evaId={}, taskId={}, throw={}", evaId, taskId, e.getMessage());
            throw new BaseException(1102, "压缩文件失败");
        }
    }

    private String archived(Evaluation evaluation, String taskId)throws IOException{
        long startTime = System.currentTimeMillis();
        LOGGER.debug("Start updateExport archived infoId={}, startTime={}", taskId, startTime);

        String tmpPath = mkdirTmp(taskId);
        LOGGER.debug("Create updateExport dir success id={},tmpPath={}", taskId, tmpPath);

        int count = itemService.count(evaluation.getId(), "", "").intValue();
        int bCount = (count + BATCH_SIZE -1) / BATCH_SIZE;

        int finishCount = 0;
        for(int i = 0 ; i < bCount; i++){
            List<EvaItem> items = itemService.query(evaluation.getId(),
                    "", "", i * BATCH_SIZE, BATCH_SIZE);

            for(EvaItem item: items){
                copy(tmpPath, item);
                finishCount++;
                progress =  finishCount * 80 / count;
            }
        }

        long useTime = (System.currentTimeMillis() - startTime)/1000;
        LOGGER.debug("Export archived success infoId={},useSeconds={}", taskId, useTime);
        return tmpPath;
    }

    private String mkdirTmp(String id)throws IOException {
        String path = properties.getResource() + "/tmp/" +id;
        File file = new File(path);
        LOGGER.debug("Mkdir updateExport dir tmpPath={}", path);
        FileUtils.forceMkdir(file);

        return path;
    }

    private void copy(String tmpPath, EvaItem item)throws IOException{
        List<Declaration> lst = declarationService.queryByEvaItemId(item.getId());
        LOGGER.debug("Get declaration resources itemId={}, size={}", item.getId(), lst.size());
        for(Declaration t: lst){
            File targetFile= targetFile(tmpPath, item, t);
            FileUtils.copyFile(new File(t.getPath()), targetFile);
        }
    }

    private File targetFile(String tmpPath, EvaItem item, Declaration t){

        File file = new File(buildZipPath(tmpPath, item.getNum(), t.getFileName()));
        if(!file.exists()){
            return file;
        }

        String baseName = FilenameUtils.getBaseName(t.getFileName());
        String extName = FilenameUtils.getExtension(t.getFileName());
        for(int i = 1; i < 100; i++){
            String newFilename = String.format("%s%02d.%s", baseName, i, extName);
            file= new File(buildZipPath(tmpPath, item.getNum(), newFilename));
            if(!file.exists()){
                return file;
            }
        }
        throw new BaseException("同名文件超过100个导出失败");
    }

    private String buildZipPath(String tmpPath, String num, String filename){
        return String.format("%s/%s/%s", tmpPath, num,filename);
    }

    private String zip(String taskId, String srcDir, String  password)throws ZipException, IOException {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        parameters.setIncludeRootFolder(false);
        String p = StringUtils.trim(password);
        if(StringUtils.isNotBlank(p)){
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        }

        String zipPath = buildZipPath(taskId);
        LOGGER.debug("Zip path id={}, path={}", taskId, zipPath);

        ZipFile zipFile = StringUtils.isBlank(p)?
                new ZipFile(zipPath): new ZipFile(zipPath, p.toCharArray());
        zipFile.addFolder(new File(srcDir), parameters);

        return zipPath;
    }

    private String buildZipPath(String id)throws IOException {
        String dirPath = properties.getResource() + "/target/";
        File dir = new File(dirPath);
        if(!dir.exists()){
            FileUtils.forceMkdir(dir);
        }
        return dirPath + id + ".zip";
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
