package com.ts.server.ods.security.limit;

/**
 * 登录限制服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public interface LoginLimitService {

    /**
     * 得到失败次数
     *
     * @param username 用户名
     * @return 失败次数
     */
    int getFail(String username);

    /**
     * 增加失败次数
     *
     * @param username 用户名
     * @return 返回失败次数
     */
    int incFail(String username);

    /**
     * 重置失败次数
     *
     * @param username 用户名
     */
    void resetFail(String username);
}
