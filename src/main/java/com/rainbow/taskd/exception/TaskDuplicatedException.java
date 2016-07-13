package com.rainbow.taskd.exception;

/**
 * 任务重复
 */
public class TaskDuplicatedException extends TaskException {

    public TaskDuplicatedException(String message) {
        super(message);
    }
}
