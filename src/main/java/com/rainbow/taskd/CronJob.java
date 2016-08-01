package com.rainbow.taskd;

public interface CronJob {
    /**
     * 定时任务
     * @return false: 本次未执行任何任务
     * @param maxJobs
     */
    boolean cron(int maxJobs);
}
