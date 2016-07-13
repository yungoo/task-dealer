# task-dealer
a task scheduler demo

# usage

> test/Main.java#testTaskSchedulerBuilder

```
TaskScheduler scheduler = TaskSchedulerBuilder.newBuilder()
        .addTaskExecutor(1, new TaskExecutor() {
            public void run(Task task) throws TaskException {
                if (task.getRetryTimes() > 0) {
                    System.out.println("-> 重试任务[" + task.getRetryTimes() + "]: " + task.getId());
                } else {
                    System.out.println("-> 执行任务: " + task.getId());
                }
                
                // simulate the workload
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
```
