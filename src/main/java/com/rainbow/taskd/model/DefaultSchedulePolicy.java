package com.rainbow.taskd.model;

/**
 * 默认任务调度策略
 */
public class DefaultSchedulePolicy implements SchedulePolicy {

    private static final long UNIT = 1000;
    private static final long DELAYS[] = {UNIT, 1 * UNIT, 5 * UNIT, 20 * UNIT, 30 * UNIT};

    private long delays[];
    private int scheduleInterval;
    private int maxConcurrentTasks;
    private int executeBatchSize;

    public DefaultSchedulePolicy() {
        this(100, 10, 10, DELAYS);
    }

    public DefaultSchedulePolicy(int maxConcurrentTasks, int executeBatchSize) {
        this(100, maxConcurrentTasks, executeBatchSize);
    }

    public DefaultSchedulePolicy(int maxConcurrentTasks, int executeBatchSize, long delays[]) {
        this(100, maxConcurrentTasks, executeBatchSize, delays);
    }

    public DefaultSchedulePolicy(int scheduleInterval, int maxConcurrentTasks, int executeBatchSize) {
        this(scheduleInterval, maxConcurrentTasks, executeBatchSize, DELAYS);
    }

    public DefaultSchedulePolicy(int scheduleInterval, int maxConcurrentTasks, int executeBatchSize, long delays[]) {
        this.scheduleInterval = scheduleInterval;
        this.maxConcurrentTasks = maxConcurrentTasks;
        this.executeBatchSize = executeBatchSize;
        this.delays = delays;
    }

    public int getExecuteBatchSize() {
        return executeBatchSize;
    }

    public void setExecuteBatchSize(int executeBatchSize) {
        this.executeBatchSize = executeBatchSize;
    }

    public int getScheduleInterval() {
        return scheduleInterval;
    }

    public void setScheduleInterval(int scheduleInterval) {
        this.scheduleInterval = scheduleInterval;
    }

    public int getMaxConcurrentTasks() {
        return maxConcurrentTasks;
    }

    public void setMaxConcurrentTasks(int maxConcurrentTasks) {
        this.maxConcurrentTasks = maxConcurrentTasks;
    }

    public boolean shouldRetry(final Task task) {
        return task.getRetryTimes() < task.getMaxRetryTimes();
    }

    public boolean shouldArchiveTask(Task task) {
        return task.getStatus() == TaskStatus.PROCESSED.ordinal()
                || (task.getRetryTimes() >= task.getMaxRetryTimes());
    }

    public long calculateRetryDelayTime(Task task) {
        if (task.getRetryTimes() >= 0 && task.getRetryTimes() < delays.length) {
            return delays[task.getRetryTimes()];
        } else {
            return 60 * UNIT;
        }
    }
}
