import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        double[] firstIn =  {40, 50, 40, 20}; //, 40, 50, 40, 20};
        double[] secondIn = {45, 30, 105, 90}; //, 45, 30, 105, 90};
        Worker worker = new Worker(firstIn, secondIn, 2);

        System.out.println("SIMPLE - " + Arrays.toString(worker.startSimpleSum()));
        System.out.println("PARALL - " + Arrays.toString(worker.startParallSum()));
    }

}
