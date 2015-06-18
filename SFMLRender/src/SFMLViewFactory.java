import org.smallbox.faraway.engine.renderer.RenderLayer;
import org.smallbox.faraway.ui.engine.*;

/**
 * Created by Alex on 27/05/2015.
 */
public class SFMLViewFactory extends ViewFactory {
    @Override
    public TextView createTextView() {
        return new SFMLTextView();
    }

    @Override
    public TextView createTextView(int width, int height) {
        return new SFMLTextView(width, height);
    }

    @Override
    public ColorView createColorView() {
        return new SFMLColorView(0, 0);
    }

    @Override
    public ColorView createColorView(int width, int height) {
        return new SFMLColorView(width, height);
    }

    @Override
    public FrameLayout createFrameLayout() {
        return new SFMLFrameLayout(0, 0);
    }

    @Override
    public FrameLayout createFrameLayout(int width, int height) {
        return new SFMLFrameLayout(width, height);
    }

    @Override
    public ImageView createImageView() {
        return new SFMLImageView();
    }

    @Override
    public RenderLayer createRenderLayer(int width, int height) {
        return new SFMLRenderLayer(width, height);
    }
}
