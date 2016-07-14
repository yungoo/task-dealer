package com.rainbow.taskd.impl;

import com.rainbow.taskd.TaskExecutor;
import com.rainbow.taskd.exception.TaskException;
import com.rainbow.taskd.model.Task;

import java.util.*;

public class TaskExecutorManager implements TaskExecutor {

    private Map<Integer, TaskExecutor> taskExecutorMap = new HashMap<Integer, TaskExecutor>();
    private Set<Integer> interestTypes;

    public TaskExecutorManager(Map<Integer, TaskExecutor> executorMap) {
        taskExecutorMap.putAll(executorMap);
        interestTypes = new HashSet<Integer>(taskExecutorMap.keySet());
    }

    public Set<Integer> getInterestTypes() {
        return interestTypes;
    }

    public void run(final Task task) throws TaskException {
        TaskExecutor executor = taskExecutorMap.get(task.getType());
        if (executor != null) {
            executor.run(task);
        }
    }
}
