package com.rainbow.taskd.exception;

/**
 * 任务执行异常
 */
public abstract class TaskException extends RuntimeException {

    public TaskException(String message) {
        super(message);
    }
}
