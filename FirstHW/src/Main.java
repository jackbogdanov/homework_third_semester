import java.util.Scanner;
import java.util.concurrent.locks.Lock;

public class Main {

    public static void main(String[] args) {

        System.out.println("Hello World!");
        Counter counter = new Counter();
        //Lock lock = new ReentrantLock();
        //PetersonLock lock = new PetersonLock();

        Lock lock = new TAS();

        MyThread[] threads = new MyThread[4];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyThread(counter, lock, i);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }


        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println(counter.toString());

    }
}
