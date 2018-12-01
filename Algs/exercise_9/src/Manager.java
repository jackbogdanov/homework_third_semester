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
    private int[] parentheses;

    private int threadsCount;

    public Manager(int[] parentheses, int threadsCount) {
        this.parentheses = parentheses;
        this.threadsCount = threadsCount;
        prepare(threadsCount);
    }

    private void prepare(int threadsCount) {
        partLength = parentheses.length / threadsCount;

        workingThreads = new ArrayList<>();

        shearingBuf = new int[threadsCount][2];
        flags = new boolean[FLAGS_COUNT][threadsCount];
        infoShifts = new int[threadsCount];

        for (int i = 0; i < infoShifts.length; i++) {
            infoShifts[i] = -1;
        }

        for (int i = 0; i < threadsCount; i++) {
            workingThreads.add(new WorkingThread(parentheses, i,
                    partLength, shearingBuf, flags, infoShifts));
        }

    }

    public boolean parallCheckOfParentheses() {

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

        return shearingBuf[threadsCount - 1][0] == 0 && shearingBuf[threadsCount - 1][1] == 0;
    }

    public boolean simpleCheckOfParentheses() {
        int extraRight = 0;
        int extraLeft = 0;

        for (int i = 0; i < parentheses.length; i++) {
            if ((extraRight + parentheses[i]) < 0) {
                extraLeft += 1;
            } else {
                extraRight += parentheses[i];
            }
        }

        return extraLeft == 0 && extraRight == 0;
    }

    public static int[] getCorrectInput(String s) {
        char[] parenthesesChars = s.toCharArray();
        int[] goodInput = new int[parenthesesChars.length];

        for (int i = 0; i < goodInput.length; i++) {
            goodInput[i] = (parenthesesChars[i] == '(') ? 1 :-1;
        }

        return goodInput;
    }
}
