package org.smallbox.faraway;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.*;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.engine.ui.View;

/**
 * Created by Alex on 27/05/2015.
 */
public class Renderer {
    private final RenderWindow _window;

    public Renderer(RenderWindow window) {
        _window = window;
    }

    public void draw(View view, RenderEffect effect) {
        //_window.draw(, effect.getRender());
    }

    public void draw(RectangleShape background, RenderEffect effect) {
        if (effect != null) {
            _window.draw(background, effect.getRender());
        } else {
            _window.draw(background);
        }
    }

    public void draw(Text text, RenderEffect effect) {
        if (effect != null) {
            _window.draw(text, effect.getRender());
        } else {
            _window.draw(text);
        }
    }

    public void draw(SpriteModel sprite, RenderEffect effect) {
        if (effect != null) {
            _window.draw(sprite.getData(), effect.getRender());
        } else {
            _window.draw(sprite.getData());
        }
    }

    public void draw(ColorView view, RenderEffect effect) {
//        _window.draw(spriteCache, effect.getRender());
    }

    public void draw(Text text) {
        _window.draw(text);
    }

    public void draw(Sprite spriteCache, RenderEffect effect) {
        if (effect != null) {
            _window.draw(spriteCache, effect.getRender());
        } else {
            _window.draw(spriteCache);
        }
    }

    public void clear(Color color) {
        _window.clear(color);
    }

    public void draw(SpriteModel sprite, RenderStates renderStates) {
        _window.draw(sprite.getData(), renderStates);
    }
}
