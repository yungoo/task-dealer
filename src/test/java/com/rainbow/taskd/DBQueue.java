package com.rainbow.taskd;

import com.rainbow.taskd.entity.CrawlTask;
import com.rainbow.taskd.exception.TaskException;
import com.rainbow.taskd.exception.TaskFailureException;
import com.rainbow.taskd.model.Task;
import com.rainbow.taskd.repository.CrawlTaskRepository;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;

@Configuration
@EnableJpaRepositories
public class DBQueue {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.H2);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan("com.rainbow.taskd.entity");
        return lef;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Test
    public void testExternalQueue() {
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

                        if (Math.random() < 0.3) {
                            throw new TaskFailureException("执行失败");
                        }

                        try {
                            Thread.sleep((long) (1000 * Math.random()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .shedulePolicy(SchedulePolicyBuilder.newBuilder().maxConcurrentTasks(4).batchSize(2).build())
                .externalQueue(new TaskQueueDelegate() {

                    public Task createTask(Task task) {
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

        for (int i = 0; i < 10; i++) {
            Task t = Task.newTask(1, 1L, new HashMap<String, Object>(), 3);
            scheduler.createTask(t);
            System.out.println("创建任务: " + t.getId());
        }

        scheduler.start();
        scheduler.join();
    }
}
