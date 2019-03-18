import java.util.concurrent.locks.Lock;

public class WorkingThread extends Thread {

    private static final int ANGLES_COLLECT = 0;
    private static final int VECTORS_COLLECT = 1;
    private static final int GET_ANGELS = 0;

    private double[] r;
    private double[] angles;
    private int partLength;
    private volatile double[][] shearingBuffer;

    private volatile int[] infoShifts; //must be -1 in the begging of work!
    private volatile int[] disturbedT;

    private boolean[][] flags;
    private int id;
    private int threadsCount;
    private Lock lock;

    public WorkingThread(double[] r, double[] angles, int id, int partLength, double[][] shearingBuffer,
                         boolean[][] flags, int[] infoShifts,
                         int[] disturbedT, Lock lock, int threadsCount) {
        this.partLength = partLength;
        this.id = id;
        this.r = r;
        this.angles = angles;
        this.shearingBuffer = shearingBuffer;
        this.flags = flags;
        this.infoShifts = infoShifts;
        this.disturbedT = disturbedT;
        this.lock = lock;
        this.threadsCount = threadsCount;
    }

    @Override
    public void run() {
        sumPart();
        waitPhaseFinish(Worker.SUM_PHASE_FINISHED);
        collectPhase(ANGLES_COLLECT);
        waitPhaseFinish(Worker.COLLECT_PHASE_FINISHED);
        distributePhase();
        waitPhaseFinish(Worker.FINISHED_DISTURB);
        updateCarry();
        waitPhaseFinish(Worker.CARRY_UPDATE);
        sumPart2();
        waitPhaseFinish(Worker.SUM2_PHASE_FINISHED);
        collectPhase(VECTORS_COLLECT);
        waitPhaseFinish(Worker.COLLECT2_PHASE_FINISHED);
    }

    private void collectPhase(int mode) {
        if (id % 2 == 0) {
            setFlag(mode);
            return;
        }
        int k = -1;
        int shearingCount = 0;

        while (k + id >= 0) {
            shearingCount++;

            while ((infoShifts[k + id] != (shearingCount - 1))) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (mode == ANGLES_COLLECT) {
                shearingBuffer[id][GET_ANGELS] = sumAngles(shearingBuffer[k + id][GET_ANGELS], shearingBuffer[id][GET_ANGELS]);
            } else {
                shearingBuffer[id] = Worker.sumFunction(shearingBuffer[k + id], shearingBuffer[id]);
            }

            infoShifts[id] += 1;

            k *= 2;

            if ((id + 1) % (k * (-2)) != 0) {
                setFlag(mode);
                return;
            }
        }

        setFlag(mode);
    }

    private void setFlag(int mode) {
        if(mode == ANGLES_COLLECT) {
            flags[Worker.COLLECT_PHASE_FINISHED][id] = true;
        } else {
            flags[Worker.COLLECT2_PHASE_FINISHED][id] = true;
        }
    }

    private void distributePhase() {
        double durValue = -1;
        int shearingDisCount;

        if (shearingBuffer[id + 1][GET_ANGELS] == Worker.SPECIAL_VALUE) {
            shearingBuffer[id + 1] = shearingBuffer[id];
            flags[Worker.START_DISTURB][id] = true;
            durValue = 0;
        }

        while (true) {
            if (flags[Worker.START_DISTURB][id]) {
                shearingDisCount = infoShifts[id];

                if (durValue == -1) {
                    durValue = shearingBuffer[id][GET_ANGELS];
                }

                int t = disturbedT[id];
                int k = (threadsCount + 1) / t;
                while (shearingDisCount > 0) {

                    lock.lock();
                    try {
                        shearingBuffer[id][GET_ANGELS] = sumAngles(durValue, shearingBuffer[id - k][GET_ANGELS]); //SWAP
                        shearingBuffer[id - k][GET_ANGELS] = durValue;
                        durValue = shearingBuffer[id][GET_ANGELS];

                        shearingDisCount--;
                        infoShifts[id - k] = shearingDisCount;

                        t = t * 2;
                        disturbedT[id - k] = t;
                        flags[Worker.START_DISTURB][id - k] = true;

                    } finally {
                        lock.unlock();
                    }

                    k = (threadsCount + 1) / t;
                }

                flags[Worker.FINISHED_DISTURB][id] = true;
                return;

            } else {
                while (!flags[Worker.START_DISTURB][id]) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void waitPhaseFinish(int phase) {
        boolean isAllFinished = false;

        while (!isAllFinished) {
            isAllFinished = true;
            for (boolean bool :
                    flags[phase]) {
                if (!bool) {
                    isAllFinished = false;
                }
            }
        }
    }

    private void sumPart() {

        for (int i = 1; i < partLength; i++) {
            angles[id * partLength + i] += angles[id * partLength + i - 1];
        }

        shearingBuffer[id][GET_ANGELS] = angles[(id + 1) * partLength - 1];
        infoShifts[id] = 0;
        flags[Worker.SUM_PHASE_FINISHED][id] = true;
    }

    private double sumAngles(double angleOne, double angleTwo) {
        return angleOne + angleTwo;
    }

    private void updateCarry() {

        double num = shearingBuffer[id][GET_ANGELS];

        for (int i = 0; i < partLength; i++) {
            angles[id * partLength + i] = angles[id * partLength + i] + num;
        }
        flags[Worker.CARRY_UPDATE][id] = true;
    }

    private void sumPart2() {
        double[] sum = Worker.toR2(r[id * partLength], angles[id * partLength]);

        for (int i = 1; i < partLength; i++) {
            sum = Worker.sumFunction(sum, Worker.toR2(r[id * partLength + i], angles[id * partLength + i]));
        }

        shearingBuffer[id] = sum;

        infoShifts[id] = 0;
        flags[Worker.SUM2_PHASE_FINISHED][id] = true;
    }

}
