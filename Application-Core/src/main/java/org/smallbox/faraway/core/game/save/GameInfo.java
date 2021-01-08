package org.smallbox.faraway.core.game.save;

import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {
    public boolean generateMountains;
    public PlanetInfo planet;
    public RegionInfo region;
    public String name;
    public List<GameSaveInfo> saveFiles = new ArrayList<>();
    public int worldWidth;
    public int worldHeight;
    public int worldFloors;
    public int groundFloor;
}
