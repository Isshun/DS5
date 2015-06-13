import org.smallbox.faraway.Color;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;

/**
 * Created by Alex on 27/05/2015.
 */
public class SFMLFrameLayout extends FrameLayout {

    public SFMLFrameLayout(int width, int height) {
        super(width, height);
    }

    @Override
    protected void createRender() {
        int posX = _x;
        int posY = _y;

        View parent = _parent;
        while (parent != null) {
            posX += parent._x;
            posY += parent._y;
            parent = parent._parent;
        }

        _renderEffect = new SFMLRenderEffect();
        _renderEffect.setTranslate(posX + _offsetX, posY + _offsetY);
    }

    @Override
    public void refresh() {
        _views.forEach(View::refresh);
    }

//    @Override
//    public void setBackgroundColor(Color color) {
//    }

    @Override
    public void setBorderColor(Color color) {
    }

    @Override
    public int getContentWidth() {
        return _width;
    }

    @Override
    public int getContentHeight() {
        return _height;
    }
}
