import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        String s = "(((())))()()())(";

        Manager manager = new Manager(Manager.getCorrectInput(s), 8);
        System.out.println("PARAL - " + manager.parallCheckOfParentheses());
        System.out.println("SIMPLE - " + manager.simpleCheckOfParentheses());

    }

}
