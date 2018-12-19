package org.sample.program;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WEB_crawler_sys implements WEB_crawler {

    private MySynchronizedSet<String> visited;
    private ExecutorService threadPool;

    private MySynchronizedSet<Integer> currentTask;
    private String startUrl;
    private int startDepth;
    private Lock lock;


    public WEB_crawler_sys(String URL, int depth, int maxThreads) {
        visited = new MySynchronizedSet<>();
        threadPool = new MyThreadPool(maxThreads);

        currentTask = new MySynchronizedSet<>();

        lock = new ReentrantLock();

        startUrl = URL;
        startDepth = depth;
    }

    public void startCrawl() {
        addNewTask(startUrl, startDepth);
    }


    public void addNewTask(String URL, int depth) {
        int id = currentTask.size() + 1;
        Runnable task = new Crawler_worker_Task(URL, this, depth, id, lock);
        visited.add(URL);

        currentTask.add(id);
        threadPool.execute(task);
    }

    public boolean isVisited(String URL) {
        return visited.contains(URL);
    }

    public void onTaskFinished(Integer id) {
        currentTask.remove(id);
        if (currentTask.isEmpty()) {
            threadPool.shutdown();
        }
    }
}
