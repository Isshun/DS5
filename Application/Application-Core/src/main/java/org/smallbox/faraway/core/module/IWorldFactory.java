package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 17/11/2016.
 */
public interface IWorldFactory {
    void create(Game game, WorldModule worldModule, RegionInfo regionInfo);
}
