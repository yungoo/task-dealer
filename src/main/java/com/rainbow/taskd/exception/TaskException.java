package com.rainbow.taskd.exception;

/**
 * Created by haiyang on 16/7/12.
 */
public abstract class TaskException extends RuntimeException {

    public TaskException(String message) {
        super(message);
    }
}
