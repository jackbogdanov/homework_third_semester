import java.util.ArrayList;

public class Manager {

    public static final int SUM_PHASE_FINISHED = 0;
    public static final int COLLECT_PHASE_FINISHED = 1;
    private static final int FLAGS_COUNT = 2;

    private ArrayList<WorkingThread> workingThreads;
    private int partLength;
    private int[][] shearingBuf;
    private int[] infoShifts;
    private boolean[][] flags;
    private int[] A;
    private int[] B;

    private int threadsCount;

    public Manager(int[] A, int[] B, int threadsCount) {
        this.A = A;
        this.B = B;
        this.threadsCount = threadsCount;
        prepare(threadsCount);
    }

    private void prepare(int threadsCount) {
        partLength = A.length / threadsCount;

        workingThreads = new ArrayList<>();

        shearingBuf = new int[threadsCount][2];
        flags = new boolean[FLAGS_COUNT][threadsCount];
        infoShifts = new int[threadsCount];

        for (int i = 0; i < infoShifts.length; i++) {
            infoShifts[i] = -1;
        }

        for (int i = 0; i < threadsCount; i++) {
            workingThreads.add(new WorkingThread(A, B, i,
                    partLength, shearingBuf, flags, infoShifts));
        }

    }

    public int startParallSum() {
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

        return shearingBuf[threadsCount - 1][1];
    }

    public int startSimpleSum() {
        int x = 0;

        for (int i = 0; i < A.length; i++) {
            x = x * A[i] + B[i];
        }
        return x;
    }
}
