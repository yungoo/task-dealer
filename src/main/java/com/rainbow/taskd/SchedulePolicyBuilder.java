package com.rainbow.taskd;

import com.rainbow.taskd.model.DefaultSchedulePolicy;
import com.rainbow.taskd.model.SchedulePolicy;

/**
 * 调度策略生产器
 */
public class SchedulePolicyBuilder {

    private DefaultSchedulePolicy policy;

    public static SchedulePolicyBuilder newBuilder() {
        return new SchedulePolicyBuilder();
    }

    private SchedulePolicyBuilder() {
        policy = new DefaultSchedulePolicy();
    }

    public SchedulePolicyBuilder scheduleInterval(int interval) {
        assert interval > 0;
        policy.setScheduleInterval(interval);
        return this;
    }

    public SchedulePolicyBuilder maxConcurrentTasks(int count) {
        assert count > 0;
        policy.setMaxConcurrentTasks(count);
        return this;
    }

    public SchedulePolicyBuilder batchSize(int size) {
        assert size > 0;
        policy.setExecuteBatchSize(size);
        return this;
    }

    public SchedulePolicy build() {
        return policy;
    }
}
