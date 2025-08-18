package com.groqdata.framework.manager;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.groqdata.common.utils.Threads;
import com.groqdata.common.utils.spring.SpringUtils;

/**
 * 异步任务管理器
 *
 * @author MISP TEAM
 */
public class AsyncManager {
    /**
     * 操作延迟10毫秒
     */
    private static final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作任务调度线程池
     */
    private final ScheduledExecutorService executor = SpringUtils.getBean("scheduledExecutorService");

    // 使用静态内部类实现线程安全的懒加载单例模式
    private static class SingletonHolder {
        private static final AsyncManager INSTANCE = new AsyncManager();
    }

    // 私有构造器，防止外部实例化
    private AsyncManager() {}

    public static AsyncManager me() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(TimerTask task) {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() {
        Threads.shutdownAndAwaitTermination(executor);
    }
}
