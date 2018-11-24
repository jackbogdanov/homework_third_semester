import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Summator {

    public static final int C = 2;
    public static final int M = 1;
    public static final int N = 0;

    public static final int SPECIAL_VALUE = -666;

    public static final int SUM_PHASE_FINISHED = 0;
    public static final int COLLECT_PHASE_FINISHED = 1;
    public static final int START_DISTURB = 2;
    public static final int FINISHED_DISTURB = 3;
    public static final int CARRY_UPDATE = 4;
    public static final int ALL_FINISHED = 5;
    private static final int FLAGS_COUNT = 6;

    private ArrayList<CountIntThread> workingThreads;
    private int partLength;
    private int[] shearingBuf;
    private int[] infoShifts;
    private int[] results;
    private boolean[][] flags;
    private int[] disturbedT;
    private int[] intA;
    private int[] intB;
    private int[] carryToNextThread;

    public Summator(int[] intA, int[] intB, int threadsCount) {
        this.intA = intA;
        this.intB = intB;
        prepare(threadsCount);
    }

    private void prepare(int threadsCount) {
        partLength = intA.length / threadsCount;

        workingThreads = new ArrayList<>();

        shearingBuf = new int[threadsCount + 1];
        results = new int[intA.length];
        flags = new boolean[FLAGS_COUNT][threadsCount];
        infoShifts = new int[threadsCount];
        disturbedT = new int[threadsCount];
        carryToNextThread = new int[threadsCount + 1];

        init();

        Lock lock = new ReentrantLock();

        for (int i = 0; i < threadsCount; i++) {
            workingThreads.add(new CountIntThread(intA, intB, i, partLength, shearingBuf,
                    results, flags, infoShifts, disturbedT, lock, threadsCount, carryToNextThread));
        }

    }

    private void init() {
        for (int i = 0; i < infoShifts.length; i++) {
            infoShifts[i] = -1;
            disturbedT[i] = 2;
        }
        infoShifts[shearingBuf.length - 2] = 0;
        shearingBuf[shearingBuf.length - 1] = SPECIAL_VALUE;
    }

    public int[] startParallSum() {
        //TO_DO summing!

        for (Thread th :
                workingThreads) {
            th.start();
        }

        for (Thread th :
                workingThreads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("FINISHED");

        return results;
    }

    public int[] startSimpleSum() {
        int[] intC = new int[intA.length];
        int carry = 0;

        for (int i = 0; i < intC.length; i++) {
            intC[i] = (intA[i] + intB[i] + carry) % 10;
            carry = (intA[i] + intB[i] + carry) / 10;
        }
        return intC;
    }
}
