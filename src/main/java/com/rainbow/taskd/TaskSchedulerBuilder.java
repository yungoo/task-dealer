package com.rainbow.taskd;

import com.rainbow.taskd.impl.*;
import com.rainbow.taskd.model.DefaultSchedulePolicy;
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

        private SchedulePolicy schedulePolicy;

        private Map<Integer, TaskExecutor> executorMap = new HashMap<Integer, TaskExecutor>();


        public SetupExecutor addTaskExecutor(int type, TaskExecutor executor) {
            executorMap.put(type, executor);

            return this;
        }

        public SetupQueue shedulePolicy(SchedulePolicy policy) {
            assert schedulePolicy == null;
            this.schedulePolicy = policy;
            return new SetupQueue(this);
        }

    }

    public static class SetupQueue {
        SetupExecutor setupExecutor;

        public SetupQueue(SetupExecutor setupExecutor) {
            this.setupExecutor = setupExecutor;
        }

        public TaskSchedulerBuilder inMemoryQueue() {
            TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
            builder.executorConfig = this.setupExecutor;
            builder.taskQueue = new InMemoryTaskQueue();

            return builder;
        }

        public TaskSchedulerBuilder externalQueue(TaskQueueDelegate delegate) {
            TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
            builder.executorConfig = this.setupExecutor;
            builder.taskQueue = new ExternalTaskQueue(delegate);

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
        SchedulePolicy policy = executorConfig.schedulePolicy;
        if (policy == null) {
            policy = new DefaultSchedulePolicy();
        }
        ts.setSchedulePolicy(policy);

        // 设置驱动器
        ts.setSchedulerDriver(new TaskSchedulerDriverImpl(policy.getScheduleInterval(),
                policy.getMaxConcurrentTasks()));

        return ts;
    }

}
