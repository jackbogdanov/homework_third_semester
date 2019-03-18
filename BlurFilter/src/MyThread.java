import java.awt.*;
import java.awt.image.BufferedImage;

public class MyThread extends Thread {

    private final int NUM_SUMMED_PIXELS = 9;

    private BufferedImage resultImage;
    private BufferedImage originImage;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public MyThread(BufferedImage resultImage, BufferedImage originImage, int startX, int startY, int numX, int numY) {
        this.resultImage = resultImage;
        this.originImage = originImage;
        this.startX = startX;
        this.startY = startY;
        endX = startX + numX;
        endY = startY + numY;
    }

    @Override
    public void run() {

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                resultImage.setRGB(i, j, getColor(i, j).getRGB());
            }
        }

        System.out.println("Thread number - " + currentThread().getId() + " finished");
    }

    private Color getColor(int x, int y) {
        Color originPixColor = new Color(originImage.getRGB(x, y));
        Color color;

        int r = 0;
        int g = 0;
        int b = 0;

        for (int i = x-1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < originImage.getWidth() && j >= 0 && j < originImage.getHeight()) {
                    color = new Color(originImage.getRGB(i, j));
                    r += color.getRed();
                    g += color.getGreen();
                    b += color.getBlue();
                } else {
                    r += originPixColor.getRed();
                    g += originPixColor.getGreen();
                    b += originPixColor.getBlue();
                }
            }
        }

        r /= NUM_SUMMED_PIXELS;
        g /= NUM_SUMMED_PIXELS;
        b /= NUM_SUMMED_PIXELS;
        return new Color(r, g, b);
    }
}
