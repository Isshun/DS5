package org.smallbox.faraway.ui.engine.views.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;

public class UIImage extends View {
    protected int _textureX;
    protected int _textureY;
    protected int _textureWidth;
    protected int _textureHeight;

    private Sprite          _sprite;
    protected String        _path;
    protected double        _scaleX = 1;
    protected double        _scaleY = 1;
    private boolean         _dirty;

    public UIImage(int width, int height) {
        super(width, height);
    }

    public void setImage(Sprite sprite) {
        _sprite = sprite;
        _dirty = true;
    }

    public void setImage(String path) {
        if (!path.equals(_path)) {
            _sprite = null;
            _path = path;
            _dirty = true;
        }
    }

    public void setScale(double scaleX, double scaleY) {
        _scaleX = scaleX;
        _scaleY = scaleY;
    }

    public void setTextureRect(int textureX, int textureY, int textureWidth, int textureHeight) {
        _textureX = textureX;
        _textureY = textureY;
        _textureWidth = textureWidth;
        _textureHeight = textureHeight;
    }

    @Override
    public void addView(View view) {
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {

            if (_dirty) {
                _dirty = false;

                if (_path != null) {
                    try {
                        _sprite = SpriteManager.getInstance().getIcon(_path);
                        _sprite.setRegion(0, 0, _width, _height);
                        _sprite.setSize(_width, _height);
                        _sprite.flip(false, true);
                    } catch (GdxRuntimeException e) {
//                e.printStackTrace();
                    }
                }

                if (_effect != null) {
                    _effect.reset(_sprite);
                }
            }

            if (_sprite != null) {
                // TODO
                if (_textureHeight != 0) {
                    _sprite.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
                    _sprite.setRegion(_textureX, _textureY, _textureWidth, _textureHeight);
                    renderer.drawRegion(_sprite, _x + x, _y + y);
                }

                else {
                    if (_effect != null && _effect._durationLeft > 0) {
                        _effect.draw(renderer, _x + x, _y + y);
                    } else if (_animation != null) {
                        _animation.draw(renderer, _sprite, _x + x, _y + y);
                    } else {
                        renderer.draw(_sprite, _x + x, _y + y);
                    }
                }
            }
        }
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

