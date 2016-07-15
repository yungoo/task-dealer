package com.rainbow.taskd.model;

/**
 * 任务状态
 */
public enum TaskStatus {

    /* 等待处理 */
    PENDING,

    /* 处理中 */
    PROCESSING,

    /* 已处理 */
    PROCESSED,

    /* 已归档 */
    ARCHIVED,
}
