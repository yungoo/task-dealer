package com.rainbow.taskd;

import com.rainbow.taskd.model.Task;

import java.util.List;
import java.util.Set;

/**
 * 队列存储代理
 */
public interface TaskQueueDelegate {

    /**
     * 创建任务
     * @param task
     */
    Task createTask(Task task);

    /**
     * 批量出队
     *
     * @param interestTypes
     * @param batchSize
     * @return
     */
    List<Task> dequeTasks(Set<Integer> interestTypes, int batchSize);

    /**
     * 更新任务
     * @param task
     */
    void updateTask(Task task);
}