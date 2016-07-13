# task-dealer(taskd)
a task dealer demo

# usage

### run taskd in memory queue

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

### run taskd in database queue

> test/DBQueue.java#testExternalQueue

```
AbstractApplicationContext context = new AnnotationConfigApplicationContext(DBQueue.class);
final CrawlTaskRepository repository = context.getBean(CrawlTaskRepository.class);

TaskScheduler scheduler = TaskSchedulerBuilder.newBuilder()
        .addTaskExecutor(1, new TaskExecutor() {
            public void run(Task task) throws TaskException {
                if (task.getRetryTimes() > 0) {
                    System.out.println("-> 重试任务[" + task.getRetryTimes() + "]: " + task.getId());
                } else {
                    System.out.println("-> 执行任务: " + task.getId());
                }

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
        .externalQueue(new TaskQueueDelegate() {

            public Task createTask(Task task) {
                CrawlTask t = new CrawlTask();
                BeanUtils.copyProperties(task, t);
                t = repository.save(t);
                task.setId(t.getId());
                return task;
            }

            public Task requeueTask(Task task) {
                CrawlTask t = new CrawlTask();
                BeanUtils.copyProperties(task, t);
                t = repository.save(t);
                task.setId(t.getId());
                return task;
            }

            @Transactional
            public List<Task> dequeTasks(int batchSize) {
                List<Task> taskList = new LinkedList<Task>();
                List<CrawlTask> tasks = repository.getBatchTasksForExecute(batchSize);
                if (tasks != null) {
                    for (CrawlTask t : tasks) {
                        Task task = new Task();
                        BeanUtils.copyProperties(t, task);
                        taskList.add(task);
                    }
                }

                return taskList;
            }

            public void updateTask(Task task) {
                CrawlTask t = new CrawlTask();
                BeanUtils.copyProperties(task, t);
                repository.save(t);
            }
        })
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


