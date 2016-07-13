package com.rainbow.taskd.impl;

import com.rainbow.taskd.TaskQueue;
import com.rainbow.taskd.TaskQueueDelegate;
import com.rainbow.taskd.model.Task;
import com.rainbow.taskd.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基于委托的queue
 *
 * 用于支持队列的存储化实现
 */
public class ExternalTaskQueue implements TaskQueue {

    private Logger logger = LoggerFactory.getLogger(ExternalTaskQueue.class);

    private TaskQueueDelegate delegate;

    public ExternalTaskQueue(TaskQueueDelegate delegate) {
        this.delegate = delegate;
    }

    public Long enque(Task task) {
        if (task.getId() != null) {
            delegate.updateTask(task);
        } else {
            task = delegate.createTask(task);
        }
        return task.getId();
    }

    public List<Task> deque(int batchSize) {
        List<Task> tasks = delegate.dequeTasks(batchSize);
        return tasks;
    }

    public void feedback(Task task) {
        if (task.getStatus() == TaskStatus.PENDING.ordinal()) {
            enque(task);
        } else if (task.getStatus() == TaskStatus.PROCESSED.ordinal() ||
                task.getStatus() == TaskStatus.ARCHIVED.ordinal()) {
            delegate.updateTask(task);
        } else {
            logger.error("处理执行反馈异常：未知的状态");
        }
    }

}
