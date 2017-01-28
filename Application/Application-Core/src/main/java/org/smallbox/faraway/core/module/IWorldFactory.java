package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;

/**
 * Created by Alex on 17/11/2016.
 */
public interface IWorldFactory {
    void create(Game game, RegionInfo regionInfo);
}
