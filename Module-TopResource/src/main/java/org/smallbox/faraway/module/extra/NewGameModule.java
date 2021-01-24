package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.planet.PlanetInfo;
import org.smallbox.faraway.game.planet.RegionInfo;

public class NewGameModule extends GameModule {
    private PlanetInfo      _planet;
    private RegionInfo      _region;

    @Override
    protected void onGameUpdate(Game game, int tick) {
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
            Application.gameManager.createGame(_region);
        }
    }
}