package com.rainbow.taskd.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

public class Task implements Serializable {

    private Long id;

    private Integer type;
    private Long requestId;
    private String params;
    private Integer status;
    private Timestamp requestTime;
    private Integer retryTimes;
    private Integer maxRetryTimes;
    private Timestamp lastExecuteTime;
    private Timestamp createTime;

    public static Task newTask(int taskType, Long requestId, Map params, int maxRetryTimes) {
        return newTask(taskType, requestId, params == null ? null : JSON.toJSONString(params), maxRetryTimes);
    }

    public static Task newTask(int taskType, Long requestId, String params, int maxRetryTimes) {
        Task task = new Task();

        task.setType(taskType);
        task.setRequestId(requestId);
        task.setParams(params);

        task.setStatus(TaskStatus.PENDING.ordinal());
        task.setRetryTimes(0);
        task.setMaxRetryTimes(maxRetryTimes);
        task.setCreateTime(new Timestamp(System.currentTimeMillis()));
        task.setRequestTime(new Timestamp(System.currentTimeMillis()));

        return task;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", type=" + type +
                ", requestId=" + requestId +
                ", params='" + params + '\'' +
                ", status=" + status +
                ", requestTime=" + requestTime +
                ", retryTimes=" + retryTimes +
                ", maxRetryTimes=" + maxRetryTimes +
                ", lastExecuteTime=" + lastExecuteTime +
                ", createTime=" + createTime +
                '}';
    }
}
