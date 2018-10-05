import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("Hello World!");
        Counter counter = new Counter();
        MyThread[] threads = new MyThread[10];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyThread(counter);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }


        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println(counter.toString());

    }
}
