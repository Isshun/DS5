package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;

/**
 * Created by Alex on 05/07/2015.
 */
public class NewGameModule extends GameModule {
    private PlanetInfo      _planet;
    private RegionInfo      _region;

    @Override
    protected void onGameStart(Game game) {
    }

    @Override
    protected void onGameUpdate(int tick) {
    }

    @Override
    public void onCustomEvent(String tag, Object object) {
        if ("new_game.planet".equals(tag) && object instanceof PlanetInfo) {
            _planet = (PlanetInfo)object;
        }
        if ("new_game.region".equals(tag) && object instanceof RegionInfo) {
            _region = (RegionInfo)object;
        }
        if ("new_game.start".equals(tag)) {
            GameManager.getInstance().create(_region);
        }
    }
}