package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;

public interface IWorldFactory {
    void create(Data data, Game game, RegionInfo regionInfo);
}
