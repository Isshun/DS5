package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.log.Log;

public abstract class AbsGameModule extends ModuleBase implements GameObserver {

    public void createGame() {
        Log.debug(getClass(), "Create game");
        _isLoaded = true;
    }

}
