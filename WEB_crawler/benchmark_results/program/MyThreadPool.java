package org.sample.program;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPool implements ExecutorService {

    private Thread[] threads;
    private MySynchronizedSet<Runnable> queue;
    private Lock lock;
    private boolean isFinished;


    public MyThreadPool(int threadsCount) {
        threads = new Thread[threadsCount];
        lock = new ReentrantLock();
        queue = new MySynchronizedSet<>();
        isFinished = false;

        for (int i = 0; i < threadsCount; i++) {
            threads[i] = new Thread(new PoolTask());
            threads[i].start();
        }

    }

    private final class PoolTask implements Runnable {

        @Override
        public void run() {

            while (!isFinished) {
                Runnable task = null;
                try {
                    lock.lock();
                    if (!queue.isEmpty()) {
                        task = queue.getFirst();
                    }
                } finally {
                    lock.unlock();
                }

                if (task != null) {
                    task.run();
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void execute(Runnable task) {
        queue.add(task);
    }

    public void shutdown() {
        isFinished = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return isFinished;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return null;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return null;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
