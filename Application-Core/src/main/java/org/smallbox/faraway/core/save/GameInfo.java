package org.smallbox.faraway.core.save;

import org.smallbox.faraway.game.planet.PlanetInfo;
import org.smallbox.faraway.game.planet.RegionInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameInfo {
    public LocalDateTime date;
    public boolean generateMountains;
    public PlanetInfo planet;
    public RegionInfo region;
    public String name;
    public List<GameSaveInfo> saveFiles = new ArrayList<>();
    public int worldWidth;
    public int worldHeight;
    public int worldFloors;
    public int groundFloor;
    public String label;
}
