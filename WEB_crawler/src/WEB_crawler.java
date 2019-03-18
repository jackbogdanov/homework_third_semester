import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WEB_crawler {

    private MySynchronizedSet<String> visited;
    private ExecutorService threadPool;

    private MySynchronizedSet<Integer> currentTask;
    private String startUrl;
    private int startDepth;
    private Lock lock;


    public WEB_crawler(String URL, int depth, int maxThreads) {
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


    public synchronized void addNewTask(String URL, int depth) {
        if (!visited.contains(URL)) {
            int id = currentTask.size() + 1;
            Runnable task = new Crawler_worker_Task(URL, this, depth, id, lock);
            visited.add(URL);

            currentTask.add(id);
            threadPool.execute(task);
        }
    }


    public void onTaskFinished(Integer id) {
        currentTask.remove(id);
        if (currentTask.isEmpty()) {
            threadPool.shutdown();
        }
    }
}
