package org.smallbox.farpoint.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.ui.engine.UIImage;
import org.smallbox.faraway.ui.engine.View;
import org.smallbox.farpoint.GDXRenderer;
import org.smallbox.farpoint.GDXSpriteModel;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXImageView extends UIImage {
    private Sprite  _sprite;
    private int     _finalX;
    private int     _finalY;

    public GDXImageView(int width, int height) {
        _width = width;
        _height = height;
    }

    @Override
    public void draw(GFXRenderer renderer, RenderEffect effect) {
        draw(renderer, 0, 0);
    }

    @Override
    public void draw(GFXRenderer renderer, int x, int y) {
        if (_sprite == null && _image != null) {
            _sprite = ((GDXSpriteModel) _image).getData();

            _finalX = x;
            _finalY = y;
            View view = this;
            while (view != null) {
                _finalX += view.getPosX();
                _finalY += view.getPosY();
                view = view.getParent();
            }
            // TODO
            if (_textureHeight != 0) {
                _sprite.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
                _sprite.setRegion(_textureX, _textureY, _textureWidth, _textureHeight);
            }
        }

        if (_sprite == null && _path != null) {
            Texture texture = new Texture(_path);

            _sprite = new Sprite();
            _sprite.setTexture(texture);
            _sprite.setRegion(0, 0, _width, _height);
            _sprite.flip(false, true);

            _finalX = x;
            _finalY = y;
            View view = this;
            while (view != null) {
                _finalX += view.getPosX();
                _finalY += view.getPosY();
                view = view.getParent();
            }
        }

        if (_sprite != null) {
            // TODO
            if (_textureHeight != 0) {
                _sprite.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
                _sprite.setRegion(_textureX, _textureY, _textureWidth, _textureHeight);
            }
            ((GDXRenderer) renderer).draw(_sprite, _finalX, _finalY);
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
    public void setImagePath(String path) {
        if (!path.equals(_path)) {
            _image = null;
            _sprite = null;
        }
        _path = path;
    }
}
