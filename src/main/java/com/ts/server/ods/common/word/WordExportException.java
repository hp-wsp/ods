package com.ts.server.ods.common.word;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Word导出异常
 *
 * <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class WordExportException extends RuntimeException {
    private final int code;
    private final String message;

    /**
     * 构造{@link WordExportException}
     *
     * @param code 错误编码
     * @param message 错误信息
     */
    public WordExportException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("message", message)
                .toString();
    }
}
