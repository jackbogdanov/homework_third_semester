import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyThread extends Thread{

    private Counter counter;
    private Lock lock;

    public MyThread(Counter counter, Lock lock) {
        this.counter = counter;
        this.lock = lock;
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