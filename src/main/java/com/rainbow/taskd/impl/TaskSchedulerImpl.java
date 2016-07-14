package com.rainbow.taskd.impl;

import com.rainbow.taskd.*;
import com.rainbow.taskd.exception.TaskException;
import com.rainbow.taskd.model.SchedulePolicy;
import com.rainbow.taskd.model.Task;
import com.rainbow.taskd.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;

public class TaskSchedulerImpl implements TaskScheduler, CronJob {

    private Logger logger = LoggerFactory.getLogger(TaskSchedulerImpl.class);

    private TaskQueue taskQueue;
    private TaskExecutor taskExecutor;
    private TaskExecutorObserver executorObserver;

    private SchedulePolicy schedulePolicy;

    private TaskSchedulerDriver schedulerDriver;

    public Long createTask(Task task) throws TaskException {
        task.setStatus(TaskStatus.PENDING.ordinal());
        task.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return taskQueue.enque(task);
    }

    public void start() {
        schedulerDriver.start(this);
    }

    public void stop() {
        schedulerDriver.stop();
    }

    public void join() {
        schedulerDriver.join();
    }

    public boolean cron() {
        int batchSize = schedulePolicy.getExecuteBatchSize();
        if (batchSize == 0) {
            return false;
        }

        final List<Task> taskList = taskQueue.deque(batchSize);
        if (taskList == null || taskList.isEmpty()) {
            return false;
        }

        for (final Task task : taskList) {
            schedulerDriver.execute(new Runnable() {
                public void run() {
                    runTask(task);
                }
            });
        }

        return true;
    }

    private void runTask(Task task) {
        notifyObserverBeforeTask(task);

        try {
            taskExecutor.run(task);

            task.setStatus(TaskStatus.PROCESSED.ordinal());

            if (schedulePolicy.shouldArchiveTask(task)) {
                scheduleArchiveTask(task);
            }

        } catch (TaskException e) {
            logger.debug("Failure when run task=[{}]", task.toString(), e);

            notifyObserverTaskFailed(task);

            if (schedulePolicy.shouldRetry(task)) {
                scheduleRetryTask(task);
            } else {
                if (schedulePolicy.shouldArchiveTask(task)) {
                    scheduleArchiveTask(task);
                } else {
                    task.setStatus(TaskStatus.PROCESSED.ordinal());
                }
            }
        } finally {
            notifyObserverAfterTask(task);

            feedbackTask(task);
        }
    }

    private void notifyObserverBeforeTask(Task task) {
        if (executorObserver != null) {
            try {
                executorObserver.onBeforeExecute(task);
            } catch (Exception e) {
                logger.debug("调用通知执行任务开始{}，异常：", task.toString(), e);
            }
        }
    }

    private void notifyObserverAfterTask(Task task) {
        if (executorObserver != null) {
            try {
                executorObserver.onAfterExecute(task);
            } catch (Exception e) {
                logger.debug("调用通知执行任务结束{}，异常：", task.toString(), e);
            }
        }
    }

    private void notifyObserverTaskFailed(Task task) {
        if (executorObserver != null) {
            try {
                executorObserver.onExecuteFailure(task);
            } catch (Exception e) {
                logger.debug("调用通知执行任务失败{}，异常：", task.toString(), e);
            }
        }
    }

    private void scheduleRetryTask(Task task) {
        task.setStatus(TaskStatus.PENDING.ordinal());
        task.setRetryTimes(task.getRetryTimes() + 1);
        task.setLastExecuteTime(new Timestamp(System.currentTimeMillis()));
        task.setRequestTime(new Timestamp(System.currentTimeMillis() + schedulePolicy.calculateRetryDelayTime(task)));
    }

    private void scheduleArchiveTask(Task task) {
        task.setStatus(TaskStatus.ARCHIVED.ordinal());
    }

    private void feedbackTask(Task task) {
        try {
            taskQueue.feedback(task);
        } catch (Exception e) {
            logger.debug("调用反馈任务执行结果{}，异常：", task.toString(), e);
        }
    }

    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setExecutorObserver(TaskExecutorObserver executorObserver) {
        this.executorObserver = executorObserver;
    }

    public void setSchedulePolicy(SchedulePolicy schedulePolicy) {
        this.schedulePolicy = schedulePolicy;
    }

    public void setSchedulerDriver(TaskSchedulerDriver schedulerDriver) {
        this.schedulerDriver = schedulerDriver;
    }
}
