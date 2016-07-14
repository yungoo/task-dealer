package com.rainbow.taskd.repository;

import com.rainbow.taskd.entity.CrawlTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CrawlTaskRepository extends JpaRepository<CrawlTask, Long>, ExtCrawlTaskRepository {

    @Query(value="select * from t_crawl_request_queue where status = :status and request_time <= :excuteTime order by request_time asc limit :batchSize",
            nativeQuery=true
    )
    List<CrawlTask> getBatchTasks(@Param("status") int status, @Param("excuteTime") Timestamp excuteTime, @Param("batchSize") int batchSize);
}
