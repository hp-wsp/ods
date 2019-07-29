package com.ts.server.ods.common.word;

import fr.opensagres.poi.xwpf.converter.core.ImageManager;

import java.io.File;

/**
 * 该生成连接规则
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
 class OdsImageManager extends ImageManager {
    private final String imageSubDir;
    private final String rootUrl;

    OdsImageManager(File baseDir, String imageSubDir, String rootUrl) {
        super(baseDir, imageSubDir);
        this.imageSubDir = imageSubDir;
        this.rootUrl = rootUrl;
    }

    @Override
    public String resolve(String uri) {
        return String.format("%s/%s/%s", rootUrl, imageSubDir, getFileName(uri));
    }

    private String getFileName(String imagePath) {
        String n = new File(imagePath).getName();
        return n;
    }
}
