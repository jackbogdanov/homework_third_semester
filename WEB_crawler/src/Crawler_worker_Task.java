import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.concurrent.locks.Lock;

public class Crawler_worker_Task implements Runnable {

    private String URL;
    private WEB_crawler mainCrawler;
    private int taskId;
    private int depth;
    private Lock lock;

    public Crawler_worker_Task(String URL, WEB_crawler mainCrawler, int depth, int id, Lock lock) {
        this.URL = URL;
        this.mainCrawler = mainCrawler;
        this.depth = depth;
        this.lock = lock;
        taskId = id;
    }

    @Override
    public void run() {
        crawl();
    }

    private void crawl() {
        Document document;

        try {
            document = Jsoup.connect(URL).get();
            Elements linksOnPage = document.select("a[href]");

            for (Element page : linksOnPage) {
                String newURL = page.attr("abs:href");
                try {
                    lock.lock();
                    if (depth > 0 && !mainCrawler.isVisited(newURL)) {
                        mainCrawler.addNewTask(newURL, depth - 1);
                    }
                } finally {
                    lock.unlock();
                }
            }
            saveToFile(document);
            mainCrawler.onTaskFinished(taskId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile(Document document) {
        File f = new File("crawler_output/file - "+taskId + ".html");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(document.outerHtml());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
