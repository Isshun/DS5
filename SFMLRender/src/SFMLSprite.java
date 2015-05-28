import org.jsfml.graphics.Sprite;
import org.smallbox.faraway.SpriteModel;

/**
 * Created by Alex on 27/05/2015.
 */
public class SFMLSprite implements SpriteModel {
    private Sprite  _data;
    private int     _x;
    private int     _y;

    public SFMLSprite() {
        _data = new Sprite();
    }

    public Sprite getData() {
        return _data;
    }

    @Override
    public void setPosition(int x, int y) {
        _x = x;
        _y = y;

        if (_data != null) {
            _data.setPosition(_x, _y);
        }
    }

    @Override
    public int getWidth() {
        return _data.getTextureRect().width;
    }

    @Override
    public int getHeight() {
        return _data.getTextureRect().height;
    }
}
