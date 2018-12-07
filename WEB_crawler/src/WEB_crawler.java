import java.util.Set;
import java.util.concurrent.*;

public class WEB_crawler {

    private Set<String> visited;
    private ExecutorService threadPool;

    private Set<Integer> currentTask;
    private String startUrl;
    private int startDepth;


    public WEB_crawler(String URL, int depth, int maxThreads) {
        visited = new ConcurrentSkipListSet<>();
        threadPool = Executors.newFixedThreadPool(maxThreads);
        currentTask = new ConcurrentSkipListSet<>();

        startUrl = URL;
        startDepth = depth;
    }

    public void startCrawl() {
        addNewTask(startUrl, startDepth);
    }


    public void addNewTask(String URL, int depth) {
        int id = currentTask.size() + 1;
        Runnable task = new Crawler_worker_Task(URL, this, depth, id);
        visited.add(URL);

        currentTask.add(id);
        threadPool.execute(task);
    }

    public boolean isVisited(String URL) {
        return visited.contains(URL);
    }

    public void finishedTask(Integer id) {
        currentTask.remove(id);
        System.out.println("REMOVED - " + currentTask.isEmpty());
        if (currentTask.isEmpty()) {
            threadPool.shutdown();
            System.out.println("OK");
        }
    }
}
