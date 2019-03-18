
public class Main {

    public static void main(String[] args) {
        int[] firstIn =  {0, 2, 3, 4, 0, 2, 3, 4};
        int[] secondIn = {1, 3, 1, 2, 1, 3, 1, 2};
        Manager manager = new Manager(firstIn, secondIn, 1);
        System.out.println("PARAL - " + manager.startParallSum());
        System.out.println("SIMPLE - " + manager.startSimpleSum());
    }
}
