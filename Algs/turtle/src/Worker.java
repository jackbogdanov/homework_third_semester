import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Worker {

    public static final int SPECIAL_VALUE = -666;

    public static final int SUM_PHASE_FINISHED = 0;
    public static final int SUM2_PHASE_FINISHED = 5;
    public static final int COLLECT_PHASE_FINISHED = 1;
    public static final int COLLECT2_PHASE_FINISHED = 6;
    public static final int START_DISTURB = 2;
    public static final int FINISHED_DISTURB = 3;
    public static final int CARRY_UPDATE = 4;
    private static final int FLAGS_COUNT = 7;

    private ArrayList<WorkingThread> workingThreads;
    private int partLength;
    private double[][] shearingBuf;
    private int[] infoShifts;
    private boolean[][] flags;
    private int[] disturbedT;
    private double[] r;
    private double[] angles;

    public Worker(double[] r, double[] angles, int threadsCount) {
        this.r = r;
        this.angles = angles;
        prepare(threadsCount);
    }

    private void prepare(int threadsCount) {
        partLength = r.length / threadsCount;

        workingThreads = new ArrayList<>();

        shearingBuf = new double[threadsCount + 1][2];
        flags = new boolean[FLAGS_COUNT][threadsCount];
        infoShifts = new int[threadsCount];
        disturbedT = new int[threadsCount];

        init();

        Lock lock = new ReentrantLock();

        for (int i = 0; i < threadsCount; i++) {
            workingThreads.add(new WorkingThread(r, angles, i, partLength, shearingBuf,
                    flags, infoShifts, disturbedT, lock, threadsCount));
        }

    }

    private void init() {
        for (int i = 0; i < infoShifts.length; i++) {
            infoShifts[i] = -1;
            disturbedT[i] = 2;
        }
        shearingBuf[shearingBuf.length - 1][0] = SPECIAL_VALUE;
    }

    public double[] startParallSum() {

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

        return fromR2(shearingBuf[shearingBuf.length - 2]);
    }

    public double[] startSimpleSum() {
        double[] sum = Worker.toR2(r[0], angles[0]);
        double t = angles[0];

        for (int i = 1; i < r.length; i++) {
            sum = sumFunction(sum, Worker.toR2(r[i], angles[i] + t));
            t += angles[i];
        }

        return fromR2(sum);
    }

    public static double[] fromR2(double[] vector) {
        double x = vector[0];
        double y = vector[1];
        return new double[]{Math.sqrt(y * y + x * x), Math.toDegrees(Math.atan(y / x))};
    }

    public static double[] toR2(double r, double angl) {
        return new double[]{r * Math.cos(Math.toRadians(angl)),
                r * Math.sin(Math.toRadians(angl))};
    }

    public static double[] sumFunction(double[] fistArg, double[] secondArg) {
        return new double[]{fistArg[0] + secondArg[0], fistArg[1] + secondArg[1]};
    }
}
