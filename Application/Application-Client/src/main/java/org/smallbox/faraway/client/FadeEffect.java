package org.smallbox.faraway.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.renderer.GDXRenderer;

/**
 * Created by Alex on 14/11/2015.
 */
public class FadeEffect {
    private Sprite  _from;
    private Sprite  _to;
    public int      _durationLeft;
    public int      _duration;

    public FadeEffect(int duration) {
        _duration = duration;
    }

    public void reset(Sprite to) {
        _from = _to;
        _to = to;
        _durationLeft = _duration;
    }

    public void draw(GDXRenderer renderer, int x, int y) {
        _durationLeft -= Gdx.graphics.getDeltaTime() * 1000;

        if (_from != null && _to != null) {
            if (_durationLeft > 0) {
                _from.setAlpha(((float) _durationLeft / _duration) * 1f);
                _to.setAlpha((1 - ((float) _durationLeft / _duration)) * 1f);
            } else {
                _from.setAlpha(0f);
                _to.setAlpha(1f);
            }
            renderer.draw(x, y, _from);
            renderer.draw(x, y, _to);
        }

        else if (_to != null) {
//            if (durationLeft > 0) {
//                _to.setAlpha((1 - ((float) durationLeft / duration)) * 1f);
//            } else {
//                _to.setAlpha(1f);
//            }
//            _to.setAlpha((1 - ((float) durationLeft / duration)) * 1f);
//            renderer.drawPixel(_to, x, y);
        }
    }
}
