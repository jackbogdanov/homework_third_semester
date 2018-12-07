package org.sample;

import java.io.*;

public class Tests {

    private int[] A;
    private int[] B;

    public Tests(int testNum) {
            readFromFile(testNum);
    }

    private void readFromFile(int num) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("tests/test_" + num + ".txt")));
            String s = reader.readLine();
            String[] ints = s.split(" ");

            A = new int[ints.length / 2];
            B = new int[ints.length / 2];
            int i = 0;
            int k = 0;

            for (String string : ints) {

                if (k % 2 == 0) {
                    A[i] = Integer.parseInt(string);
                } else {
                    B[i] = Integer.parseInt(string);
                    i++;
                }
                k = (k + 1) % 2;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getA() {
        return A;
    }

    public int[] getB() {
        return B;
    }
}
