package com.rainbow.taskd.impl;


import com.rainbow.taskd.CronJob;
import com.rainbow.taskd.TaskSchedulerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskSchedulerDriverImpl implements TaskSchedulerDriver {

    private Logger logger = LoggerFactory.getLogger(TaskSchedulerDriverImpl.class);

    private int scheduleInterval = 100;
    private int maxConcurrentTasks = 10;
    private AtomicBoolean mainThreadStoped = new AtomicBoolean(false);
    private AtomicBoolean isLastCronIdle = new AtomicBoolean(false);
    private AtomicBoolean isJoin = new AtomicBoolean(false);

    private ThreadPoolExecutor executorService;

    public TaskSchedulerDriverImpl(int scheduleInterval, int maxConcurrentTasks) {
        this.scheduleInterval = scheduleInterval;
        this.maxConcurrentTasks = maxConcurrentTasks;
    }

    public void start(final CronJob cronJob) {
        if (executorService == null) {
            synchronized (this) {
                if (executorService == null) {
                    executorService = new ThreadPoolExecutor(maxConcurrentTasks, maxConcurrentTasks,
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>());

                    executorService.execute(new Runnable() {
                        public void run() {
                            logger.debug("调度线程开始运行");
                            while (!mainThreadStoped.get()) {
                                int activeCount = executorService.getActiveCount();
                                int maxCount = executorService.getMaximumPoolSize();
                                if (activeCount != maxCount) {
                                    boolean hasWork = cronJob.cron();
                                    isLastCronIdle.compareAndSet(false, !hasWork);
                                }

                                if (isJoin.get() && isLastCronIdle.get()) {
                                    break;
                                }

                                try {
                                    Thread.sleep(scheduleInterval);
                                } catch (InterruptedException e) {
                                }
                            }
                            logger.debug("调度线程已终止");

                            executorService.shutdown();
                        }
                    });
                }
            }
        }
    }

    public void stop() {
        if (executorService != null) {
            mainThreadStoped.set(true);

            if (!executorService.isShutdown()) {
                try {
                    executorService.awaitTermination(60, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    logger.debug("终止任务驱动器异常", e);
                }
            }
            logger.debug("任务驱动器已终止");
        }
    }

    public void join() {
        if (executorService != null) {
            isJoin.compareAndSet(false, true);
            try {
                executorService.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.debug("终止任务驱动器异常", e);
            }
            executorService = null;
            logger.debug("任务驱动器已终止");
        }
    }

    public void execute(final Runnable runnable) {
        assert executorService != null;
        executorService.execute(runnable);
    }
}
