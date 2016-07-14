package com.rainbow.taskd.repository;

import com.rainbow.taskd.entity.CrawlTask;
import com.rainbow.taskd.model.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Component
public class CrawlTaskRepositoryImpl implements ExtCrawlTaskRepository {

    @Autowired
    private CrawlTaskRepository crawlTaskRepository;

    @Transactional
    public List<CrawlTask> getBatchTasksForExecute(int batchSize) {
        final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        List<CrawlTask> tasks = crawlTaskRepository.getBatchTasks(TaskStatus.PENDING.ordinal(),
                currentTime,
                batchSize);
        for (CrawlTask t : tasks) {
            t.setStatus(TaskStatus.PROCESSING.ordinal());
            t.setLastExecuteTime(currentTime);
        }
        crawlTaskRepository.save(tasks);
        return tasks;
    }

}
