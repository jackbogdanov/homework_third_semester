import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyThread extends Thread{

    private Counter counter;
    private Lock lock;
    private int id;

    public MyThread(Counter counter, Lock lock, int id) {
        this.counter = counter;
        this.lock = lock;
        this.id = id;
    }

    @Override
    public void run() {

        lock.lock();

        try {
            for (int i = 0; i < 100000; i++) {
                counter.inc();
            }
        }finally {
            lock.unlock();
        }

        System.out.println("Finished - " + Thread.currentThread().getId());
    }

}