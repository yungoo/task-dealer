package com.rainbow.taskd;

import com.rainbow.taskd.exception.TaskException;
import com.rainbow.taskd.model.Task;

/**
 * 任务执行器
 */
public interface TaskExecutor {

    void run(Task task) throws TaskException;

}
