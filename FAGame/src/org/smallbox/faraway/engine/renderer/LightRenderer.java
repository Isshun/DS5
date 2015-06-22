package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.GameObserver;

/**
 * Created by Alex on 04/06/2015.
 */
public abstract class LightRenderer extends BaseRenderer implements GameObserver {
    public abstract void init();
    public abstract void setSunColor(Color color);
}
