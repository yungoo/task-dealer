package com.rainbow.taskd;

import com.rainbow.taskd.exception.TaskException;
import com.rainbow.taskd.model.Task;

/**
 * 任务调度器
 */
public interface TaskScheduler {

    /**
     * 创建任务
     *
     * @param task
     * @return
     * @throws TaskException
     */
    Long createTask(Task task) throws TaskException;

    /**
     * 开始任务调度
     */
    void start();

    /**
     * 停止任务调度
     */
    void stop();

    /**
     * 等待任务完成并退出
     *
     * 注意：不能保证一定能全部执行
     */
    void join();
}
