package filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RemoveBlueFilter extends BaseFilter {

    public RemoveBlueFilter() {
        super("Remove Blue");
    }

    @Override
    public Color getPixelColor(BufferedImage image, int x, int y) {
        Color originPixColor = new Color(image.getRGB(x, y));

        return new Color(originPixColor.getRed(), originPixColor.getGreen(), 0);
    }
}
