package com.sig.tonglisecurity.task;

import android.os.Handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {
    //核心池的大小
    private static final int CORE_POOL_SIZE = 5;
    //最大核心池的大小
    private static final int MAXIMUM_POOL_SIZE = 15;
    //保持活着的线程数
    private static final int KEEP_ALIVE = 5;

    // BlockingQueue，如果 BlockingQueue 是空的，从 BlockingQueue 取东西的操作
    // 将会被阻断进入等待状态，直到 BlockingQueue 进了东西才会被唤醒，同样，如果 BlockingQueue
    // 是满的，任何试图往里存东西的操作也会被阻断进入等待状态，直到 BlockingQueue 里有空间时才
    // 会被唤醒继续操作。
    private static final BlockingQueue<Runnable> sWorkQueue =
            new LinkedBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE);

    //单例模式 -- 保证全应用只有一个 ThreadFactory 实例
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        //AtomicInteger: 原子整型
        private AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            int count = mCount.getAndIncrement();
            return new Thread(r, count + "_thread");
        }
    };

    private static RejectedExecutionHandler rejectedExecutionHandler
            = new ThreadPoolExecutor.CallerRunsPolicy();
    //线程池执行器
    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            sWorkQueue, sThreadFactory, rejectedExecutionHandler);
    private static Controller controller = null;
    private Handler _controlHandler = null;

    /*
     * 获取 Controller 的引用 (单例模式)
     */
    public static Controller getInstance() {
        if (controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    public Handler getControlHandler() {
        if (_controlHandler != null) {
            return _controlHandler;
        }
        return null;
    }

    /**
     * 执行的线程人物
     *
     * @param task 任务
     */
    public synchronized void execute(UIAsyncTask task) {
        sExecutor.execute(task);
    }
}
