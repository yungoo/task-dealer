package com.rainbow.taskd.exception;

/**
 * 任务执行失败异常
 */
public class TaskFailureException extends TaskException {

    public TaskFailureException(String message) {
        super(message);
    }

}
