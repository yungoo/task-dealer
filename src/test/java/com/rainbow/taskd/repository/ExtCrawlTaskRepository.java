package com.rainbow.taskd.repository;

import com.rainbow.taskd.entity.CrawlTask;

import java.util.List;
import java.util.Set;

/**
 * 自定义实现接口
 */
public interface ExtCrawlTaskRepository {

    List<CrawlTask> getBatchTasksForExecute(Set<Integer> interestTypes, int batchSize);

}
