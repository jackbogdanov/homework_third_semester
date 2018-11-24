import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        int[] firstInt =  {9, 9, 9, 5, 7, 5, 1, 3, 1, 2, 6, 6, 7, 5, 1, 3, 1, 2, 6, 6, 1, 6, 5, 2};
        int[] secondInt = {1, 0, 0, 1, 2, 6, 8, 6, 8, 2, 3, 6, 2, 6, 8, 6, 8, 2, 3, 6, 1, 2, 5, 1};
        Summator summator = new Summator(firstInt, secondInt, 4);
        System.out.println("PARAL - " + Arrays.toString(summator.startParallSum()));
        System.out.println("SIMPLE - " + Arrays.toString(summator.startSimpleSum()));



        System.out.println("Hello World!");
    }
}
