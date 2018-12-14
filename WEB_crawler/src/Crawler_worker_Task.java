import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class Crawler_worker_Task implements Runnable {

    private String URL;
    private WEB_crawler mainCrawler;
    private int taskId;
    private int depth;


    public Crawler_worker_Task(String URL, WEB_crawler mainCrawler, int depth, int id) {
        this.URL = URL;
        this.mainCrawler = mainCrawler;
        this.depth = depth;
        taskId = id;
    }

    @Override
    public void run() {
        crawl();
    }

    private void crawl() {
        //mainCrawler.addToVisited(URL);

        Document document;
        try {
            document = Jsoup.connect(URL).get();
            Elements linksOnPage = document.select("a[href]");

            System.out.println("FINISHED LOAD");
            for (Element page : linksOnPage) {
                String newURL = page.attr("abs:href");
                if (depth > 0 && !mainCrawler.isVisited(newURL)) {
                    System.out.println("ADDED - " + URL);
                    mainCrawler.addNewTask(newURL, depth - 1);
                }

                System.out.println("IN ELEMS");
            }

            System.out.println("FINISHED");

            saveToFile(document);
            mainCrawler.finishedTask(taskId);
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
