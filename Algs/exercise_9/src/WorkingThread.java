public class WorkingThread extends Thread {

    private int[] parentheses;

    private int partLength;
    private volatile int[][] shearingBuffer;
    private volatile int[] infoShifts; //must be -1 in the begging of work!

    private boolean[][] flags;
    private int id;

    public WorkingThread(int[] parentheses, int id, int partLength, int[][] shearingBuffer,
                         boolean[][] flags, int[] infoShifts) {
        this.partLength = partLength;
        this.id = id;
        this.parentheses = parentheses;
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
        int extraRight = 0;
        int extraLeft = 0;

        for (int i = 0; i < partLength; i++) {
            if ((extraRight + parentheses[id * partLength + i]) < 0) {
                extraLeft += 1;
            } else {
                extraRight += parentheses[id * partLength + i];
            }
        }

        shearingBuffer[id][0] = extraRight;
        shearingBuffer[id][1] = extraLeft;

        infoShifts[id] = 0;
        flags[Manager.SUM_PHASE_FINISHED][id] = true;
    }

    private int[] sumFunction(int[] fistArg, int[] secondArg) {

        int extraRight;
        int extraLeft;

        if (fistArg[0] >= secondArg[0]) {
            extraRight = fistArg[0] - secondArg[1] + secondArg[0];
            extraLeft = fistArg[1];
        } else {
            extraRight = 0;
            extraLeft = fistArg[1] + secondArg[1] - fistArg[0];
        }


        return new int[]{extraRight, extraLeft};
    }

}