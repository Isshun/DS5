package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.GameObserver;

/**
 * Created by Alex on 04/06/2015.
 */
public abstract class LightRenderer extends BaseRenderer implements GameObserver {
    public abstract void init();
    public abstract void setSunColor(Color color);
}
