package com.haoutil.xposed.haoblocker.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static ExecutorService sThreadPool = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        sThreadPool.execute(runnable);
    }
}
