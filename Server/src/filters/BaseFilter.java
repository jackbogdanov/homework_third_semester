package filters;

import java.awt.image.BufferedImage;

public abstract class BaseFilter implements IFilter {

    private String filterName;

    public BaseFilter(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }
}
