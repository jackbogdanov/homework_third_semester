package filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface IFilter {
    String getFilterName();
    Color getPixelColor(BufferedImage image, int x, int y);
}
