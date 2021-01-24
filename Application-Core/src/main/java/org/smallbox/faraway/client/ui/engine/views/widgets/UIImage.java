package org.smallbox.faraway.client.ui.engine.views.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.smallbox.faraway.client.render.BaseRendererManager;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;

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

    public UIImage(ModuleBase module) {
        super(module);
    }

    public UIImage setImage(Sprite sprite) {
        _sprite = sprite;
        _dirty = true;
        return this;
    }

    public UIImage setImage(GraphicInfo graphicInfo) {
        _sprite = spriteManager.getNewSprite(graphicInfo);
        _dirty = true;
        return this;
    }

    public UIImage setImage(String path) {
        if (path != null && !path.equals(_path)) {
            _sprite = null;
            _path = path;
            _dirty = true;
        }
        return this;
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
    public void draw(BaseRendererManager renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {

            if (_dirty) {
                _dirty = false;

                if (_path != null) {
                    try {
                        _sprite = spriteManager.getIcon(_path);
                        _sprite.setRegion(0, 0, geometry.getOriginWidth(), geometry.getOriginHeight());
                        _sprite.setSize(geometry.getOriginWidth(), geometry.getOriginHeight());
                        _sprite.setScale((float) applicationConfig.uiScale);
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
                    renderer.drawSprite(_sprite, geometry.getFinalX(), geometry.getFinalY());
//                    renderer.drawRegion(_x + x, _y + y, _sprite);
                }

                else {
                    if (_effect != null && _effect._durationLeft > 0) {
                        _effect.draw(renderer, geometry.getX() + x, geometry.getY() + y);
                    } else if (_animation != null) {
                        _animation.draw(renderer, _sprite, geometry.getFinalX(), geometry.getFinalY());
                    } else {
                        renderer.drawSprite(_sprite, geometry.getFinalX(), geometry.getFinalY());
//                        renderer.draw(_x + x, _y + y, _sprite);
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

    public static UIImage create(ModuleBase module) {
        return new UIImage(module);
    }

    public static View createFast(String image, int width, int height) {
        return create(null)
                .setImage(image)
                .setSize(width, height);
    }
}

