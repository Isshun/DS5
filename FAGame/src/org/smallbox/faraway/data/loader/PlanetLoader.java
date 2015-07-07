package org.smallbox.faraway.data.loader;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.planet.PlanetInfo;
import org.smallbox.faraway.game.model.planet.RegionInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 02/06/2015.
 */
public class PlanetLoader implements IDataLoader {
    @Override
    public void reloadIfNeeded(GameData data) {

    }

    @Override
    public void load(GameData data) {
        try {
            List<PlanetInfo> planets = new ArrayList<>();

            for (File file: new File("data/planets/").listFiles()) {
                if (file.getName().endsWith(".yml")) {
                    InputStream input = new FileInputStream(file);
                    Yaml yaml = new Yaml(new Constructor(PlanetInfo.class));
                    PlanetInfo planet = (PlanetInfo)yaml.load(input);

                    if (planet.regions != null) {
                        for (RegionInfo region : planet.regions) {
                            // Set planet
                            region.planet = planet;

                            // Set terrain type id
                            for (RegionInfo.RegionTerrain terrain : region.terrains) {
                                terrain.typeId = -1;
                                if (terrain.type != null) {
                                    switch (terrain.type) {
                                        case "sand":
                                            terrain.typeId = 1;
                                            break;
                                        case "underwater_sand":
                                            terrain.typeId = 2;
                                            break;
                                        case "grass":
                                            terrain.typeId = 3;
                                            break;
                                        case "ice":
                                            terrain.typeId = 4;
                                            break;
                                    }
                                }
                            }
                        }
                    }

                    planets.add(planet);
                }
            }

            data.planets = planets;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
