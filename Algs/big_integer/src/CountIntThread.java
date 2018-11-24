import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class CountIntThread extends Thread {

    private int[] intA;
    private int[] intB;
    private int[] summed;
    private int partLength;
    private volatile int[] shearingBuffer;
    private int[] shifts;
    private volatile int[] infoShifts; //must be -1 in the begging of work!
    private volatile int[] disturbedT;
    private volatile int[] carryToNextThread;

    private boolean[][] flags;
    private int id;
    private int threadsCount;
    private Lock lock;

    public CountIntThread(int[] intA, int[] intB, int id, int partLength, int[] shearingBuffer,
                          int[] summed, boolean[][] flags, int[] infoShifts,
                          int[] disturbedT, Lock lock, int threadsCount,
                          int[] carryToNextThread) {
        this.partLength = partLength;
        this.id = id;
        this.intA = intA;
        this.intB = intB;
        this.summed = summed;
        this.shearingBuffer = shearingBuffer;
        this.flags = flags;
        this.infoShifts = infoShifts;
        this.disturbedT = disturbedT;
        shifts = new int[partLength];
        this.lock = lock;
        this.threadsCount = threadsCount;
        this.carryToNextThread = carryToNextThread;
    }

    @Override
    public void run() {
        sumPart();
        countShearingSum();
        waitPhaseFinish(Summator.SUM_PHASE_FINISHED);
        collectPhase();
        waitPhaseFinish(Summator.COLLECT_PHASE_FINISHED);
        distributePhase();
        waitPhaseFinish(Summator.FINISHED_DISTURB);
        updateCarry();
        waitPhaseFinish(Summator.CARRY_UPDATE);
        findResult();
        waitPhaseFinish(Summator.ALL_FINISHED);
    }

    private void collectPhase() {
        if (id % 2 == 0) {
            flags[Summator.COLLECT_PHASE_FINISHED][id] = true;
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

            shearingBuffer[id] = sumShiftsFun(shearingBuffer[k + id], shearingBuffer[id]);
            infoShifts[id] += 1;

            k *= 2;

            if ((id + 1) % (k * (-2)) != 0) {
                flags[Summator.COLLECT_PHASE_FINISHED][id] = true;
                return;
            }
        }

        flags[Summator.COLLECT_PHASE_FINISHED][id] = true;
    }

    private void distributePhase() {
        int durValue = -1;
        int shearingDisCount;

        if (shearingBuffer[id + 1] == Summator.SPECIAL_VALUE) {
            shearingBuffer[id + 1] = shearingBuffer[id];
            flags[Summator.START_DISTURB][id] = true;
            durValue = Summator.M;
        }

        while (true) {
            if (flags[Summator.START_DISTURB][id]) {
                shearingDisCount = infoShifts[id];

                if (durValue == -1) {
                    durValue = shearingBuffer[id];
                }

                int t = disturbedT[id];
                int k = (threadsCount + 1) / t;
                while (shearingDisCount > 0) {

                    lock.lock();
                    try {
                        shearingBuffer[id] = sumShiftsFun(durValue, shearingBuffer[id - k]); //SWAP
                        shearingBuffer[id - k] = durValue;
                        durValue = shearingBuffer[id];

                        shearingDisCount--;
                        infoShifts[id - k] = shearingDisCount;

                        t = t * 2;
                        disturbedT[id - k] = t;
                        flags[Summator.START_DISTURB][id - k] = true;

                    } finally {
                        lock.unlock();
                    }

                    k = (threadsCount + 1) / t;
                }

                flags[Summator.FINISHED_DISTURB][id] = true;
                return;

            } else {
                while (!flags[Summator.START_DISTURB][id]) {
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
        for (int i = 0; i < partLength; i++) {
            shifts[i] = getShift(intA[id * partLength + i] + intB[id * partLength + i]);
        }
    }

    private void countShearingSum() {
        int sum = Summator.M;

        for (int i = 0; i < partLength; i++) {
            sum = sumShiftsFun(sum, shifts[i]);
            shifts[i] = sum;
        }

        shearingBuffer[id] = sum;
        infoShifts[id] = 0;
        flags[Summator.SUM_PHASE_FINISHED][id] = true;
    }

    private int sumShiftsFun (int shiftOne, int shiftTwo) {
        if (shiftOne == shiftTwo) {
            return shiftOne;
        }

        if (shiftOne == Summator.M || shiftTwo == Summator.M) {
            return shiftOne == Summator.M ? shiftTwo : shiftOne;
        }

        return shiftTwo;
    }

    private int getShift(int sum) {
        if (sum == 9) {
            return Summator.M;
        } else if (sum < 9) {
            return Summator.N;
        } else {
            return Summator.C;
        }
    }

    private void updateCarry() {
        int num = shearingBuffer[id];

        for (int i = 0; i < shifts.length; i++) {
            shifts[i] = sumShiftsFun(num, shifts[i]);
        }
        carryToNextThread[id + 1] = shifts[shifts.length - 1];
        flags[Summator.CARRY_UPDATE][id] = true;
    }

    private void findResult() {
        if (carryToNextThread[id] == Summator.C) {
            summed[id * partLength] = (intA[id * partLength] + intB[id * partLength] + 1) % 10;
        } else {
            summed[id * partLength] = (intA[id * partLength] + intB[id * partLength]) % 10;
        }

        for (int i = 1; i < shifts.length; i++) {
            if (shifts[i - 1] == Summator.C) {
                summed[id * partLength + i] = (intA[id * partLength + i] + intB[id * partLength + i] + 1) % 10;
            } else {
                summed[id * partLength + i] = (intA[id * partLength + i] + intB[id * partLength + i]) % 10;
            }
        }

        flags[Summator.ALL_FINISHED][id] = true;
    }

}
