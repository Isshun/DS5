import org.jsfml.graphics.RectangleShape;
import org.jsfml.system.Vector2f;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.ui.engine.ColorView;
import org.smallbox.faraway.ui.engine.View;

/**
 * Created by Alex on 28/05/2015.
 */
public class SFMLColorView extends ColorView {
    private RectangleShape _background;

    public SFMLColorView(int width, int height) {
        super(width, height);
    }

    @Override
    public void draw(GFXRenderer renderer, RenderEffect effect) {
        if (_background != null) {
            ((SFMLRenderer) renderer).draw(_background, effect);
        }
    }

    @Override
    public void refresh() {
    }

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    @Override
    public void init() {
    }

    @Override
    public View findById(String id) {
        return null;
    }

    @Override
    public void setBackgroundColor(Color color) {
        if (color != null) {
            _background = new RectangleShape();
            _background.setSize(new Vector2f(_width, _height));
            _background.setPosition(new Vector2f(_x, _y));
            _background.setFillColor(new org.jsfml.graphics.Color(color.r, color.g, color.b, color.a));
        } else {
            _background = null;
        }
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);

        if (_background != null) {
            _background.setPosition(x, y);
        }
    }
}
