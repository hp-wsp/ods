package com.ts.server.ods;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.util.Zip4jUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 测试Zip压缩
 *
 * @author <a href="mailto:hhywangwei@mgail.com">WangWei</a>
 */
public class ZipTest {

    //@Test
    public void testZip(){
        try{
            zip("", "filename.zip", true, "1234578");
        }catch (ZipException e){
            System.out.println(e.getMessage());
        }
    }

    private void zip(String src, String target, boolean isCreateDir, String password)throws ZipException {
        //File srcFile = new File(src);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        if(StringUtils.isNotBlank(password)){
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
           // parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        }
        ZipFile zipFile = StringUtils.isBlank(password)?
                new ZipFile(target): new ZipFile(target, password.toCharArray());
        zipFile.addFolder(new File("d:/data"), parameters);

        System.out.println(zipFile.getFile().getPath());
    }

    private List<File> addFiles(){
        return Arrays.asList(new File("d:/data/init.sql"), new File("d:/data/test.txt"));
    }

}
