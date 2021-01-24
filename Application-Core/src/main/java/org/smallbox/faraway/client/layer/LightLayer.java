package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.core.game.GameObserver;

public abstract class LightLayer extends BaseLayer implements GameObserver {
    public abstract void init();
}
