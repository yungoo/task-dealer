package com.rainbow.taskd.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name="t_crawl_request_queue")
public class CrawlTask {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Integer type;

    @Column(name = "request_id")
    private Long requestId;
    private String params;
    private Integer status;
    @Column(name = "request_time")
    private Timestamp requestTime;
    @Column(name = "retry_times")
    private Integer retryTimes;
    @Column(name = "max_retry_times")
    private Integer maxRetryTimes;
    @Column(name = "last_execute_time")
    private Timestamp lastExecuteTime;
    @Column(name = "create_time")
    private Timestamp createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Integer getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(Integer maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public Timestamp getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(Timestamp lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
