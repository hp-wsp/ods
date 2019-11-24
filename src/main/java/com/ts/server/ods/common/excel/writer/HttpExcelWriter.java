package com.ts.server.ods.common.excel.writer;

import com.ts.server.ods.common.utils.HttpUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 实现把Excel写http请求
 *
 * <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 *
 * @param <T>
 */
public abstract class HttpExcelWriter<T> extends ExcelWriter<T> {
    private final HttpServletResponse response;
    private final String filename;
    private final boolean is2003;

    /**
     * 构造{@link HttpExcelWriter}
     *
     * @param response {@link HttpServletResponse}
     * @param is2003 true:2003 excel
     * @param filename 文件名
     * @throws IOException {@link IOException}
     */
    public HttpExcelWriter(HttpServletResponse response, boolean is2003, String filename)throws IOException {
        super(response.getOutputStream(), is2003);
        this.response = response;
        this.filename = filename;
        this.is2003 = is2003;
    }

    @Override
    public void close() throws IOException {
        String suffer = is2003? "xls": "xlsx";
        HttpUtils.setContentDisposition(response, filename, suffer);
        response.setContentType(getContentType(is2003));
         super.close();
    }

    private String getContentType(boolean is2003){
        return is2003?
                "application/vnd.ms-excel":
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }
}
