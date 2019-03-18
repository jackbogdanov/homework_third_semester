package org.sample;

import java.io.*;

public class Tests {

    private int[] A;
    private int[] B;

    public Tests(int testNum) {
            readFromFile(testNum);
    }

    private void readFromFile(int num) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File("tests/test_" + num + ".txt")));
            String s = reader.readLine();
            A = new int[s.length() / 2];
            B = new int[s.length() / 2];
            int i = 0;
            int k = 0;

            for (char ch : s.toCharArray()) {

                if (k % 2 == 0) {
                    A[i] = ch - 48;
                } else {
                    B[i] = ch - 48;
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
