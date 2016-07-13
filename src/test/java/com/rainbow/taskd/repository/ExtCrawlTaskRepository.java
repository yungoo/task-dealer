package com.rainbow.taskd.repository;

import com.rainbow.taskd.entity.CrawlTask;

import java.util.List;

/**
 * Created by haiyang on 16/7/13.
 */
public interface ExtCrawlTaskRepository {

    List<CrawlTask> getBatchTasksForExecute(int batchSize);

}
