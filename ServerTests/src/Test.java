import program.Client;

import java.io.File;
import java.util.Arrays;
import java.util.stream.LongStream;

public class Test {
    private int parallelTasksCount;

    public Test(int parallelTasksCount) {
        this.parallelTasksCount = parallelTasksCount;
    }

    public void startTest() {
        File small = new File("images/small.png");
        File mid = new File("images/mid.png");
        File large = new File("images/large.png");

        long[] r1 = test(small);
        long[] r2 = test(mid);
        long[] r3 = test(large);

        printRes("Small", r1);
        printRes("Mid", r2);
        printRes("Large", r3);
    }

    private long[] test(File file) {
        long[] results = new long[3];
        for (int i = 0; i < 3; i++) {
            Thread[] thread = new Thread[parallelTasksCount];
            thread[0] = new Thread(new Client(file, "Blur Filter", i, results));
            thread[0].start();
            for (int j = 1; j <= parallelTasksCount - 1; j++) {
                thread[j] = new Thread(new Client(file, "Blur Filter", 0, null));
                thread[j].start();
            }


            try {
                for (Thread aThread : thread) {
                    aThread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    private void printRes(String mode, long[] res) {
        Arrays.sort(res);

        System.out.println(mode + " parallel clients - "
                + parallelTasksCount + " results: average time - " + LongStream.of(res).average()   //Arrays.stream(res).average()
                + " median time - " + res[1]);

        System.out.println(mode + " parallel clients - "
                + parallelTasksCount + " all results: " + Arrays.toString(res));
    }

}
