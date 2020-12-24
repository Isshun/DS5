package org.smallbox.faraway.client.render;

import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.GameObserver;

public abstract class LightLayer extends BaseLayer implements GameObserver {
    public abstract void init();
    public abstract void setSunColor(ColorUtils color);
}
