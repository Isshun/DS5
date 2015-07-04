package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.ui.engine.UILabel;

/**
 * Created by Alex on 28/05/2015.
 */
public abstract class RenderLayer {
    protected int       _x;
    protected int       _y;
    protected int       _index;
    protected boolean   _needRefresh;

    public RenderLayer(int index) {
        _index = index;
    }

    public abstract void onDraw(GFXRenderer renderer, RenderEffect renderEffect, int x, int y);
    public abstract void draw(SpriteModel sprite);
    public abstract void draw(UILabel text);
    public void begin() {}
    public void end() {}

    public void setPosition(int x, int y) {
        _x = x;
        _y = y;
    }

    public abstract boolean needRefresh();
    public abstract boolean isDrawable();

    public int getIndex() { return _index; }

    public void setRefresh() {
        _needRefresh = false;
    }

    public abstract void planRefresh();

    public abstract void refresh();
}
