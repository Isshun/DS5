package org.smallbox.faraway.game.model.planet;

import org.smallbox.faraway.game.model.planet.PlanetModel;
import org.smallbox.faraway.game.model.planet.RegionInfo;

/**
 * Created by Alex on 26/06/2015.
 */
public class RegionModel {
    private final PlanetModel   _planet;
    private final RegionInfo    _info;

    public RegionModel(PlanetModel planet, RegionInfo regionInfo) {
        _info = regionInfo;
        _planet = planet;
    }

    public PlanetModel getPlanet() {
        return _planet;
    }

    public RegionInfo getInfo() {
        return _info;
    }
}
