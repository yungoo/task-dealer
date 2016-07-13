package com.rainbow.taskd.model;

/**
 * 任务调度策略
 */
public interface SchedulePolicy {

    /**
     * 执行调度时每次获取的任务数量
     *
     * @return 任务数量
     */
    int getExecuteBatchSize();

    /**
     * 调度的频率
     *
     * @return 毫秒数
     */
    int getScheduleInterval();

    /**
     * 并行执行任务的线程最大数量
     *
     * @return 最大线程数
     */
    int getMaxConcurrentTasks();

    /**
     * 任务是否应该继续重试
     * @param task
     * @return 是否应该继续重试
     */
    boolean shouldRetry(final Task task);

    /**
     * 是否应该将任务移出队列
     * @param task
     * @return 是否应该将任务移出队列
     */
    boolean shouldArchiveTask(Task task);

    /**
     * 计算任务重试的延时时间
     * @param task
     * @return 延时毫秒数
     */
    long calculateRetryDelayTime(Task task);

}
