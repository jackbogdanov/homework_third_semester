package org.sample.program;


import java.util.concurrent.locks.ReentrantLock;

public interface WEB_crawler {

    void startCrawl();

    void addNewTask(String URL, int depth);

    boolean isVisited(String URL);

    void onTaskFinished(Integer id);
}
