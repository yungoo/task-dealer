package com.rainbow.taskd;

import com.rainbow.taskd.model.Task;

/**
 * 任务执行状态反馈
 */
public interface TaskExecutorObserver {

    /**
     * 准备执行
     * @param task
     */
    void onBeforeExecute(Task task);

    /**
     * 执行完毕
     * @param task
     */
    void onAfterExecute(Task task);

    /**
     * 执行失败
     * @param task
     */
    void onExecuteFailure(Task task);

}
