package org.smallbox.farpoint.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.renderer.RenderLayer;
import org.smallbox.faraway.engine.ui.ImageView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.farpoint.GDXRenderer;
import org.smallbox.farpoint.GDXSpriteModel;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXImageView extends ImageView {
    private Sprite  _sprite;
    private int     _finalX;
    private int     _finalY;

    @Override
    public void draw(GFXRenderer renderer, RenderEffect effect) {
        if (_sprite == null && _image != null) {
            _sprite = ((GDXSpriteModel)_image).getData();

            _finalX = 0;
            _finalX = 0;
            View view = this;
            while (view != null) {
                _finalX += view.getPosX();
                _finalX += view.getPosY();
                view = view.getParent();
            }
//            _sprite.setPosition(x, y);
            ((GDXRenderer)renderer).draw(_sprite, _finalX, _finalY);
            return;
        }

        if (_sprite == null && _path != null) {
            Texture texture = new Texture(_path);

            _sprite = new Sprite();

            _finalX = 0;
            _finalX = 0;
            View view = this;
            while (view != null) {
                _finalX += view.getPosX();
                _finalX += view.getPosY();
                view = view.getParent();
            }
//            _sprite.setPosition(x, y);
            _sprite.setTexture(texture);
            //_sprite.setTextureRect(new IntRect(0, 0, _width, _height));
            _sprite.setScale((float)_scaleX, (float)_scaleY);
        }
        if (_sprite != null) {
            ((GDXRenderer)renderer).draw(_sprite, _finalX, _finalY);
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
