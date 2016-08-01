package com.rainbow.taskd.impl;


import com.rainbow.taskd.CronJob;
import com.rainbow.taskd.TaskSchedulerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskSchedulerDriverImpl implements TaskSchedulerDriver {

    private Logger logger = LoggerFactory.getLogger(TaskSchedulerDriverImpl.class);

    private int scheduleInterval = 100;
    private int maxConcurrentTasks = 10;
    private AtomicBoolean requireToExit = new AtomicBoolean(false);
    private AtomicBoolean idling = new AtomicBoolean(false);
    private AtomicBoolean joining = new AtomicBoolean(false);
    private Object waitingForTask = new Object();
    private Object readyToExit = new Object();

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
                            while (!requireToExit.get()) {
                                int activeCount = executorService.getActiveCount();
                                int maxCount = executorService.getMaximumPoolSize();

                                if (activeCount != maxCount) {
                                    final boolean hasWork = cronJob.cron(maxCount - activeCount);
                                    idling.compareAndSet(false, !hasWork);
                                }

                                if (joining.get() && idling.get()) {
                                    break;
                                }

                                if (activeCount == maxCount || idling.get()) {
                                    try {
                                        synchronized (waitingForTask) {
                                            waitingForTask.wait(scheduleInterval);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            executorService.shutdown();
                            // 通知线程结束
                            synchronized (readyToExit) {
                                readyToExit.notify();
                            }

                            logger.debug("调度线程已终止");
                        }
                    });
                }
            }
        }
    }

    public void stop() {
        if (executorService != null) {
            requireToExit.set(true);

            if (!executorService.isShutdown()) {
                try {
                    synchronized (readyToExit) {
                        readyToExit.wait();
                    }
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
            joining.compareAndSet(false, true);
            try {
                synchronized (readyToExit) {
                    readyToExit.wait();
                }
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
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } finally {
                    synchronized (waitingForTask) {
                        waitingForTask.notify();
                    }
                }
            }
        });
    }
}
