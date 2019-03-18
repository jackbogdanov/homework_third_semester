import java.util.concurrent.locks.Lock;

public class PetersonLock {

    private boolean[] flag;
    private int victim;

    public PetersonLock() {
        flag = new boolean[2];
        victim = -1;

    }

    public void lock(int id) {
        int anotherOne = 1 - id;

        victim = id;

        while (flag[anotherOne] && victim == id) {};
    }

    public void unlock(int id) {
        flag[id] = false;
    }
}
