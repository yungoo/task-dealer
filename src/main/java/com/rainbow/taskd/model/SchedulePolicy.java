package com.rainbow.taskd.model;

/**
 * 任务调度策略
 */
public class SchedulePolicy {

    private static final long UNIT = 1000;
    private static final long DELAYS[] = {UNIT, 1 * UNIT, 5 * UNIT, 20 * UNIT, 30 * UNIT};

    private int executeBatchSize;

    public int getExecuteBatchSize() {
        return executeBatchSize;
    }

    public void setExecuteBatchSize(int executeBatchSize) {
        this.executeBatchSize = executeBatchSize;
    }

    public boolean shouldRetry(final Task task) {
        return task.getRetryTimes() < task.getMaxRetryTimes();
    }

    public boolean shouldArchiveTask(Task task) {
        return task.getStatus() == TaskStatus.PROCESSED.ordinal()
                || (task.getRetryTimes() >= task.getMaxRetryTimes());
    }

    public long calculateRetryAfter(Task task) {
        if (task.getRetryTimes() >= 0 && task.getRetryTimes() < DELAYS.length) {
            return DELAYS[task.getRetryTimes()];
        } else {
            return 60000*60L;
        }
    }
}
