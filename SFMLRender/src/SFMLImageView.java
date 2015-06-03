import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.ImageView;
import org.smallbox.faraway.engine.ui.View;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alex on 28/05/2015.
 */
public class SFMLImageView extends ImageView {

    private Sprite _sprite;

    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect effect) {

    }

    @Override
    public void draw(GFXRenderer renderer, RenderEffect effect) {
        if (_sprite == null && _image != null) {
            _sprite = ((SFMLSprite)_image).getData();

            int x = 0;
            int y = 0;
            View view = this;
            while (view != null) {
                x += view.getPosX();
                y += view.getPosY();
                view = view.getParent();
            }
            _sprite.setPosition(x, y);
            ((SFMLRenderer)renderer).draw(_sprite);
            return;
        }

        if (_sprite == null && _path != null) {
            try {
                Texture texture = new Texture();
                texture.loadFromFile(new File(_path).toPath());
                texture.setSmooth(true);

                _sprite = new Sprite();

                int x = 0;
                int y = 0;
                View view = this;
                while (view != null) {
                    x += view.getPosX();
                    y += view.getPosY();
                    view = view.getParent();
                }
                _sprite.setPosition(x, y);
                _sprite.setTexture(texture);
                _sprite.setTextureRect(new IntRect(0, 0, _width, _height));
                _sprite.setScale((float)_scaleX, (float)_scaleY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (_sprite != null) {
            ((SFMLRenderer)renderer).draw(_sprite);
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
}
