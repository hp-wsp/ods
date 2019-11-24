package com.ts.server.ods.common.excel.reader;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 读取Excel结果
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class ReadResult {
    private AtomicInteger count;
    private List<RowError> rowErrors;

    /**
     * 构造{@link ReadResult}
     */
    public ReadResult(){
        this.count = new AtomicInteger();
        this.rowErrors = new ArrayList<>(10);
    }

    /**
     * 增加导入记录数
     *
     * @return 导入记录数
     */
    public int incCount(){
        return count.incrementAndGet();
    }

    /**
     * 增加导入错误信息
     *
     * @param line 导入错误行
     * @param msg 导入错误信息
     */
    public synchronized void addErrorRow(int line, String msg){
        rowErrors.add(new RowError(line, msg));
    }

    /**
     * 得到导入行数
     *
     * @return 导入行数
     */
    public int getCount(){
        return count.get();
    }

    /**
     * 得到错误信息集合
     *
     * @return 错误信息集合
     */
    public synchronized List<RowError> getErrorRows(){
        return rowErrors;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("count", count)
                .append("rowErrors", rowErrors)
                .toString();
    }

    public static class RowError {
        /**
         * 错误行数
         */
        private final int line;

        /**
         * 错误消息
         */
        private final String msg;

        /**
         * 导入错误行
         *
         * @param line 错误行数
         * @param msg 错误消息
         */
        public RowError(int line, String msg) {
            this.line = line;
            this.msg = msg;
        }

        public int getLine() {
            return line;
        }

        public String getMsg() {
            return msg;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("line", line)
                    .append("msg", msg)
                    .toString();
        }
    }
}
