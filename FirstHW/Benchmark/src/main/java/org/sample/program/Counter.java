package org.sample.program;

public class Counter {

    private int count = 0;

    public void inc() {

        count++;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "count=" + count +
                '}';
    }
}
