package filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BlurFilter extends BaseFilter{
    private final int NUM_SUMMED_PIXELS = 9;

    public BlurFilter() {
        super("Blur Filter");
    }

    @Override
    public Color getPixelColor(BufferedImage image, int x, int y) {
        Color originPixColor = new Color(image.getRGB(x, y));
        Color color;

        int r = 0;
        int g = 0;
        int b = 0;

        for (int i = x-1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < image.getWidth() && j >= 0 && j < image.getHeight()) {
                    color = new Color(image.getRGB(i, j));
                    r += color.getRed();
                    g += color.getGreen();
                   // b += color.getBlue();
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
