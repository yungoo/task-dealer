package com.rainbow.taskd;

import com.rainbow.taskd.exception.TaskException;
import com.rainbow.taskd.model.Task;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;

public class Main {

    @Test
    public void testTaskSchedulerBuilder() {
        final TaskScheduler scheduler = TaskSchedulerBuilder.newBuilder()
                .addTaskExecutor(1, new TaskExecutor() {
                    public void run(Task task) throws TaskException {
                        double havy = 1.0 * Math.random();
                        if (task.getRetryTimes() > 0) {
                            System.out.println("-> 重试任务[" + task.getRetryTimes() + "]: " + task.getId());
                        } else {
                            System.out.println("-> 执行任务: " + task.getId() + " about:" + havy + " seconds");
                        }

//                        if (Math.random() < 0.5) {
//                            throw new TaskFailureException("wtf");
//                        }

                        try {
                            Thread.sleep((long) (havy * 1000L));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .shedulePolicy(SchedulePolicyBuilder.newBuilder().maxConcurrentTasks(100).batchSize(100).build())
                .inMemoryQueue()
                .build();

        assertNotNull(scheduler);

        final long tasksCount = 1000;
        for (int i = 0; i < tasksCount; i++) {
            Task t = Task.newTask(1, 1L, new HashMap<String, Object>(), 3);
            scheduler.createTask(t);
        }
        System.out.println("已创建任务: " + tasksCount);

        long begin = System.currentTimeMillis();
        scheduler.start();
        scheduler.join();

        long duration = (System.currentTimeMillis() - begin);
        double tps = ((double)tasksCount) / duration * 1000;
        System.out.println("任务已全部完成, tps=" + tps);
    }

}
