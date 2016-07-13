package com.rainbow.taskd;

import com.rainbow.taskd.model.Task;

import java.util.List;

/**
 * 队列接口
 */
public interface TaskQueue {

    /**
     * 入队
     * @param task
     * @return
     */
    Long enque(Task task);

    /**
     * 出队
     * @param batchSize
     * @return
     */
    List<Task> deque(int batchSize);

    /**
     * 执行后反馈
     * @param task
     */
    void feedback(Task task);
}
