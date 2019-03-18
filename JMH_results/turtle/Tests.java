package org.sample;

import java.io.*;

public class Tests {

    private double[] r;
    private double[] ang;

    public Tests(int testNum) {
            readFromFile(testNum);
    }

    private void readFromFile(int num) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("tests/test_" + num + ".txt")));
            String s = reader.readLine();
            String[] ints = s.split(" ");

            r = new double[ints.length / 2];
            ang = new double[ints.length / 2];
            int i = 0;
            int k = 0;


            for (String string : ints) {

                if (k % 2 == 0) {
                    r[i] = Integer.parseInt(string);
                } else {
                    ang[i] = Integer.parseInt(string);
                    i++;
                }

                k = (k + 1) % 2;
            }
        } catch (Exception e) {

        }
    }

    public double[] getR() {
        return r;
    }

    public double[] getAng() {
        return ang;
    }

}
