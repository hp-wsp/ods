package com.ts.server.ods.exec;

/**
 * 扩展{@link Runnable}，增加运行进度方法
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public interface ProgressRunnable extends Runnable {

    /**
     * 运行进度
     *
     * @return 运行进度
     */
    int progress();
}
