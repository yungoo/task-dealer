package com.rainbow.taskd;

/**
 * 任务调度驱动器
 */
public interface TaskSchedulerDriver {

    void start(CronJob job);

    void execute(Runnable runnable);

    void stop();

    void join();

}
