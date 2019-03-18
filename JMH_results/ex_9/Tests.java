package org.sample;

import java.io.*;

public class Tests {

    private int[] parentheses;

    public Tests(int testNum) {
            readFromFile(testNum);
    }

    private void readFromFile(int num) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("tests/test_" + num + ".txt")));
            String s = reader.readLine();
            String[] ints = s.split(" ");

            parentheses = new int[ints.length];

            int i = 0;


            for (String string : ints) {
                parentheses[i] = Integer.parseInt(string);
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getParentheses() {
        return parentheses;
    }

}
