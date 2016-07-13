package com.rainbow.taskd.repository;

import com.daze.tade.model.TaskStatus;
import com.rainbow.taskd.entity.CrawlTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CrawlTaskRepositoryImpl implements ExtCrawlTaskRepository {

    @Autowired
    private CrawlTaskRepository crawlTaskRepository;

    @Transactional
    public List<CrawlTask> getBatchTasksForExecute(int batchSize) {
        List<CrawlTask> tasks = crawlTaskRepository.getBatchTasks(TaskStatus.PENDING.ordinal(), batchSize);
        for (CrawlTask t : tasks) {
            t.setStatus(TaskStatus.PROCESSING.ordinal());
        }
        crawlTaskRepository.save(tasks);
        return tasks;
    }

}
