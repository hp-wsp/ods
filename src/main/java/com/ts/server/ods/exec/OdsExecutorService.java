package com.ts.server.ods.exec;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.exec.domain.ExecLog;
import com.ts.server.ods.exec.service.ExecLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 长时间执行任务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com>WangWei</a>
 */
@Service
public class OdsExecutorService implements DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(OdsExecutorService.class);

    private final ExecLogService logService;
    private final ExecutorService executorService;
    private final Timer delayTimer ;
    private final Map<String, ProgressRunnable> taskPool;

    /**
     * 构造{@link OdsExecutorService}
     *
     * @param logService {@link ExecLogService}
     */
    @Autowired
    public OdsExecutorService(ExecLogService logService){
        this.logService = logService;
        this.executorService = Executors.newFixedThreadPool(2);
        this.delayTimer = new Timer("DELAY_REMOVE_PROGRESS");
        this.taskPool = new ConcurrentHashMap<>(6);
    }

    public synchronized void submit(String taskKey, String remark, ProgressRunnable runnable){
        boolean success = addTask(taskKey, runnable);
        LOGGER.debug("Add task success = {}", success);
        if(!success){
            throw new BaseException(305, "任务正在执行请稍等 ...");
        }
        Future<?> future = executorService.submit(createRunner(taskKey, remark, runnable));
        LOGGER.debug("Submit task success taskId={}, isDone={}", taskKey, future.isDone());
    }

    private boolean addTask(String taskKey, ProgressRunnable runnable){
        ProgressRunnable o = taskPool.get(taskKey);
        if(o != null && o.progress() < 100){
            return false;
        }

        taskPool.put(taskKey, runnable);
        return true;
    }

    private Runnable createRunner(String taskKey, String remark,  ProgressRunnable runnable){
        ExecLog log = logService.createTask(taskKey, remark);
        return () -> {
            boolean ok = true;

            try{
                runnable.run();
                logService.success(log.getId());
            }catch (Exception e){
                LOGGER.debug("Exec task fail taskId={},throw={}", taskKey, e.getMessage());
                taskPool.remove(taskKey);
                logService.fail(log.getId(), StringUtils.left(e.getMessage(), 200));
                ok = false;
            }

            if(ok){
                delayTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        taskPool.remove(taskKey);
                    }
                }, 30000L);
            }
        };
    }

    public int progress(String taskKey){
        ProgressRunnable runnable = taskPool.get(taskKey);
        return runnable == null? -1 : runnable.progress();
    }

    @Override
    public void destroy() {
        executorService.shutdown();
    }
}
