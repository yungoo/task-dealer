package com.rainbow.taskd.exception;

/**
 * 任务终止
 */
public class TaskAbortException extends TaskException {

    public TaskAbortException(String message) {
        super(message);
    }
}
