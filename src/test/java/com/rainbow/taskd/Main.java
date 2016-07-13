package com.rainbow.taskd;

import com.rainbow.taskd.exception.TaskException;
import com.rainbow.taskd.model.Task;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;

public class Main {

    @Test
    public void testTaskSchedulerBuilder() {
        TaskScheduler scheduler = TaskSchedulerBuilder.newBuilder()
                .addTaskExecutor(1, new TaskExecutor() {
                    public void run(Task task) throws TaskException {
                        if (task.getRetryTimes() > 0) {
                            System.out.println("-> 重试任务[" + task.getRetryTimes() + "]: " + task.getId());
                        } else {
                            System.out.println("-> 执行任务: " + task.getId());
                        }

//                        if (Math.random() < 0.5) {
//                            throw new TaskFailureException("wtf");
//                        }

                        try {
                            Thread.sleep((long) (1000*Math.random()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .batchSize(2)
                .maxConcurrentTasks(10)
                .scheduleInterval(100)
                .inMemoryQueue()
                .build();

        assertNotNull(scheduler);

        for (int i = 0; i < 100; i++) {
            Task t = Task.newTask(1, 1L, new HashMap<String, Object>(), 3);
            scheduler.createTask(t);
            System.out.println("创建任务: " + t.getId());
        }

        scheduler.start();
        scheduler.join();
    }

}
