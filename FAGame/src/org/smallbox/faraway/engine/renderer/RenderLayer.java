package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.ui.TextView;

/**
 * Created by Alex on 28/05/2015.
 */
public abstract class RenderLayer {
    public abstract void clear();
    public abstract void onDraw(GFXRenderer renderer, RenderEffect renderEffect);
    public abstract void draw(SpriteModel sprite);
    public abstract void draw(TextView text);
    public void end() {}
}
