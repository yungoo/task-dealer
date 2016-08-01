package com.rainbow.taskd.impl;

import com.rainbow.taskd.TaskQueue;
import com.rainbow.taskd.model.Task;
import com.rainbow.taskd.model.TaskStatus;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于内存的任务队列
 */
public class InMemoryTaskQueue implements TaskQueue {


    private PriorityQueue<Task> tasks = new PriorityQueue<Task>(new Comparator<Task>() {
        public int compare(Task o1, Task o2) {
            return o1.getRequestTime().compareTo(o2.getRequestTime());
        }
    });

    private ReentrantLock lock = new ReentrantLock();
    private AtomicLong _id = new AtomicLong(1L);

    public Long enque(Task task) {
        if (task.getId() == null) {
            Long id = _id.getAndIncrement();
            task.setId(id);
        }

        lock.lock();
        try {
            if (tasks.offer(task)) {
                return task.getId();
            }
        } finally {
            lock.unlock();
        }

        return null;
    }

    public List<Task> deque(Set<Integer> interestTypes, int batchSize) {
        final Date now = new Date();
        final List<Task> ret = new LinkedList<Task>();

        lock.lock();
        try {
            while (batchSize-- > 0) {
                Task t = tasks.peek();
                if (t != null
                        && interestTypes.contains(t.getType())
                        && t.getRequestTime().before(now)) {
                    ret.add(tasks.poll());
                } else {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }

        return ret;
    }

    public void feedback(Task task) {
        if (task.getStatus() == TaskStatus.PENDING.ordinal()) {
            enque(task);
        }
    }
}
