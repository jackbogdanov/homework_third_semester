import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ThreadManager {

    public static final int HORIZONTAL_MODE = 0;
    public static final int VERTICAL_MODE = 1;

    private BufferedImage resultImage;
    private BufferedImage originImage;
    private int height;
    private int width;

    public ThreadManager(BufferedImage image) {
        originImage = image;
        height = image.getHeight();
        width = image.getWidth();
        resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void startTreads(int threadsCount, int mode) {

        MyThread[] threads = createThreads(threadsCount, mode);

        for (MyThread thread: threads) {
            thread.start();
        }

        for (MyThread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Image processing completed!");

    }

    private MyThread[] createThreads (int threadsCount, int mode) {
        if (mode == HORIZONTAL_MODE) {
            return createHorizontalThreads(threadsCount);
        }

        if (mode == VERTICAL_MODE) {
            return createVerticalThreads(threadsCount);
        }

        System.out.println("Unknown mode!!");

        return null;
    }

    private MyThread[] createVerticalThreads(int threadsCount) {
        int gapSize = width / threadsCount;

        MyThread[] threads = new MyThread[threadsCount];

        for (int i = 0; i < threadsCount; i++) {
            threads[i] = new MyThread(resultImage, originImage, i * gapSize, 0, gapSize, height);
        }

        return threads;
    }

    private MyThread[] createHorizontalThreads(int threadsCount) {
        int gapSize = height / threadsCount;

        MyThread[] threads = new MyThread[threadsCount];

        for (int i = 0; i < threadsCount; i++) {
            threads[i] = new MyThread(resultImage, originImage, 0, i * gapSize, width, gapSize);
            System.out.println("GapSize - " + gapSize + ", startY - " + i * gapSize);
        }

        return threads;
    }

    public void saveToFile(String name) throws IOException {
        ImageIO.write(resultImage, "bmp", new File(name + ".bmp"));
    }
}
