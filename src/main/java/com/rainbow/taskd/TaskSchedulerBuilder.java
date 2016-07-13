package com.rainbow.taskd;

import com.rainbow.taskd.impl.TaskExecutorManager;
import com.rainbow.taskd.impl.InMemoryTaskQueue;
import com.rainbow.taskd.impl.TaskSchedulerDriverImpl;
import com.rainbow.taskd.impl.TaskSchedulerImpl;
import com.rainbow.taskd.model.SchedulePolicy;

import java.util.HashMap;
import java.util.Map;

public class TaskSchedulerBuilder {

    private SetupExecutor executorConfig;
    private TaskQueue taskQueue;

    /**
     * 设置执行的线程数量
     */
    public static SetupExecutor newBuilder() {
        return new SetupExecutor();
    }

    public static class SetupExecutor {

        private int maxConcurrentTasks;
        private int batchSize;
        private int scheduleInterval;
        private Map<Integer, TaskExecutor> executorMap = new HashMap<Integer, TaskExecutor>();

        public SetupExecutor addTaskExecutor(int type, TaskExecutor executor) {
            executorMap.put(type, executor);

            return this;
        }

        public SetupExecutor maxConcurrentTasks(int maxConcurrentTasks) {
            this.maxConcurrentTasks = maxConcurrentTasks;

            return this;
        }

        public SetupExecutor batchSize(int batchSize) {
            this.batchSize = batchSize;

            return this;
        }


        public SetupExecutor scheduleInterval(int interval) {
            this.scheduleInterval = batchSize;

            return this;
        }

        public TaskSchedulerBuilder inMemoryQueue() {
            TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
            builder.executorConfig = this;
            builder.taskQueue = new InMemoryTaskQueue();

            return builder;
        }

    }

    public TaskScheduler build() {
        TaskSchedulerImpl ts = new TaskSchedulerImpl();

        // 设置任务执行器
        TaskExecutor te = new TaskExecutorManager(executorConfig.executorMap);
        ts.setTaskExecutor(te);

        // 设置队列
        ts.setTaskQueue(taskQueue);

        // 设置调度策略
        SchedulePolicy policy = new SchedulePolicy();
        policy.setExecuteBatchSize(executorConfig.batchSize);
        ts.setSchedulerPolicy(policy);

        // 设置驱动器
        ts.setSchedulerDriver(new TaskSchedulerDriverImpl(executorConfig.scheduleInterval,
                executorConfig.maxConcurrentTasks));

        return ts;
    }

}
