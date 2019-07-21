package com.ts.server.ods.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP工具类
 *
 * <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class HttpUtils {

    /**
     * 获取客户端访问地址
     *
     * @param request {@link HttpServletRequest}
     * @return 客户端地址
     */
    public static String getHostname(HttpServletRequest request){
        String[] httpHeads = new String[]{"X-Real-IP","X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP"};

        String hostname = "";

        for(String head: httpHeads){
            String v = request.getHeader(head);
            if(StringUtils.isNotBlank(v) && !StringUtils.equalsIgnoreCase(v, "unknown")){
                hostname = v;
                break;
            }
        }

        if(StringUtils.isBlank(hostname)){
            hostname = request.getRemoteAddr();
        }

        return StringUtils.isBlank(hostname)? "0.0.0.0" : hostname;
    }
}
