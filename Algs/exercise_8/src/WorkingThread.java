public class WorkingThread extends Thread {

    private int[] A;
    private int[] B;

    private int partLength;
    private volatile int[][] shearingBuffer;
    private volatile int[] infoShifts; //must be -1 in the begging of work!

    private boolean[][] flags;
    private int id;

    public WorkingThread(int[] A, int[] B, int id, int partLength, int[][] shearingBuffer,
                         boolean[][] flags, int[] infoShifts) {
        this.partLength = partLength;
        this.id = id;
        this.A = A;
        this.B = B;
        this.shearingBuffer = shearingBuffer;
        this.flags = flags;
        this.infoShifts = infoShifts;
    }

    @Override
    public void run() {
        sumPart();
        waitPhaseFinish(Manager.SUM_PHASE_FINISHED);
        collectPhase();
        waitPhaseFinish(Manager.COLLECT_PHASE_FINISHED);
    }

    private void collectPhase() {
        if (id % 2 == 0) {
            flags[Manager.COLLECT_PHASE_FINISHED][id] = true;
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

            shearingBuffer[id] = sumFunction(shearingBuffer[k + id], shearingBuffer[id]);
            infoShifts[id] += 1;

            k *= 2;

            if ((id + 1) % (k * (-2)) != 0) {
                flags[Manager.COLLECT_PHASE_FINISHED][id] = true;
                return;
            }
        }

        flags[Manager.COLLECT_PHASE_FINISHED][id] = true;
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
        int a = A[id * partLength];
        int b = B[id * partLength];
        for (int i = 1; i < partLength; i++) {
            a *= A[id * partLength + i];
            b = A[id * partLength + i] * b + B[id * partLength + i];
        }

        shearingBuffer[id][0] = a;
        shearingBuffer[id][1] = b;

        infoShifts[id] = 0;
        flags[Manager.SUM_PHASE_FINISHED][id] = true;
    }

    private int[] sumFunction(int[] fistArg, int[] secondArg) {
        return new int[]{fistArg[0] * secondArg[0],
                secondArg[0] * fistArg[1] + secondArg[1]};
    }

}