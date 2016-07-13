package com.rainbow.taskd.impl;

import com.rainbow.taskd.TaskExecutor;
import com.rainbow.taskd.exception.TaskException;
import com.rainbow.taskd.model.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskExecutorManager implements TaskExecutor {

    private Map<Integer, TaskExecutor> taskExecutorMap = new HashMap<Integer, TaskExecutor>();

    public TaskExecutorManager(Map<Integer, TaskExecutor> executorMap) {
        taskExecutorMap.putAll(executorMap);
    }

    public void run(final Task task) throws TaskException {
        TaskExecutor executor = taskExecutorMap.get(task.getType());
        if (executor != null) {
            executor.run(task);
        }
    }
}
