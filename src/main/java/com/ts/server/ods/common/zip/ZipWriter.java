package com.ts.server.ods.common.zip;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.util.zip.Deflater;

/**
 * zip方式压缩
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ZipWriter implements Closeable {

    /**
     * 构建{@link ZipWriter}
     */
    public static class Builder {
        private final OutputStream outputStream;
        private Zip64Mode zip64Mode = Zip64Mode.AsNeeded;
        private int level = Deflater.DEFAULT_COMPRESSION;

        /**
         * 构造{@link Builder}
         *
         * @param outputStream 输出数据流
         */
        public Builder(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        /**
         * 构造{@link Builder}
         *
         * @param file 输出文件
         * @throws IOException
         */
        public Builder(File file)throws IOException{
            try(OutputStream outputStream = new FileOutputStream(file)){
                this.outputStream = outputStream;
            }
        }

        /**
         * 设置64位压缩模式
         *
         * @param zip64Mode {@link Zip64Mode}
         * @return {@link Builder}
         */
        public Builder setMode64(Zip64Mode zip64Mode){
            this.zip64Mode = zip64Mode;
            return this;
        }

        /**
         * 设置压缩模式
         *
         * @param level 压缩级别
         * @return {@link Builder}
         */
        public Builder setLevel(int level){
            this.level = level;
            return this;
        }

        /**
         * 构建{@link ZipWriter}
         *
         * @return {@link ZipWriter}
         */
        public ZipWriter build(){
            return new ZipWriter(outputStream, zip64Mode, level);
        }
    }

    private final ZipArchiveOutputStream zipStream;

    /**
     * 构造{@link ZipWriter}
     *
     * @param outputStream 输出数据流
     * @param zip64Mode {@link Zip64Mode}
     * @param level 压缩级别
     */
    private ZipWriter(OutputStream outputStream, Zip64Mode zip64Mode, int level){
        zipStream = new ZipArchiveOutputStream(outputStream);
        zipStream.setUseZip64(zip64Mode);
        zipStream.setUseLanguageEncodingFlag(true);
        zipStream.setLevel(level);
    }

    /**
     * 添加被压缩文件
     *
     * @param file 文件
     *
     * @throws IOException
     */
    public void addEntry(File file)throws IOException{
        String name = file.getName();
        addEntry(file, name);
    }

    /**
     * 添加被压缩文件
     *
     * @param file 被压缩文件
     * @param name 压缩文件名
     *
     * @throws IOException
     */
    public void addEntry(File file, String name)throws IOException{
        try(InputStream inputStream= new FileInputStream(file)){
            addEntry(inputStream, file.length(), name);
        }
    }

    /**
     * 添加被压缩输入流
     *
     * @param inputStream 被压缩输入流
     * @param size 输入流文件尺寸
     * @param name 压缩文件名
     *
     * @throws IOException
     */
    public void addEntry(InputStream inputStream, long size, String name)throws IOException{
        ZipArchiveEntry entry = new ZipArchiveEntry(name);
        entry.setSize(size);
        entry.setTime(System.currentTimeMillis());
        zipStream.addRawArchiveEntry(entry, inputStream);
    }

    @Override
    public void close() throws IOException {
        finish();
    }

    /**
     * 压缩完成
     *
     * @throws IOException
     */
    private void finish()throws IOException{
        zipStream.finish();
        zipStream.flush();
        zipStream.close();
    }
}
