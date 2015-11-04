//package org.smallbox.faraway.core.data.loader;
//
//import org.smallbox.faraway.core.game.model.Data;
//import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
//import org.smallbox.faraway.core.game.model.planet.RegionInfo;
//import org.smallbox.faraway.core.util.FileUtils;
//import org.yaml.snakeyaml.Yaml;
//import org.yaml.snakeyaml.constructor.Constructor;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.util.ArrayList;
//
///**
// * Created by Alex on 02/06/2015.
// */
//public class PlanetLoader implements IDataLoader {
//
//    @Override
//    public void reloadIfNeeded(Data data) {
//    }
//
//    @Override
//    public void load(Data data) {
//        data.planets = new ArrayList<>();
//        FileUtils.list("data/planets/").stream().filter(file -> file.getName().endsWith(".yml")).forEach(file -> {
//            try {
//                InputStream input = new FileInputStream(file);
//                Yaml yaml = new Yaml(new Constructor(PlanetInfo.class));
//                PlanetInfo planet = (PlanetInfo)yaml.load(input);
//
//                if (planet.regions != null) {
//                    for (RegionInfo region : planet.regions) {
//                        // Set planet
//                        region.planet = planet;
//
//                        // Set terrain type id
//                        for (RegionInfo.RegionTerrain terrain : region.terrains) {
//                            terrain.typeId = -1;
//                            if (terrain.type != null) {
//                                switch (terrain.type) {
//                                    case "sand":
//                                        terrain.typeId = 1;
//                                        break;
//                                    case "underwater_sand":
//                                        terrain.typeId = 2;
//                                        break;
//                                    case "grass":
//                                        terrain.typeId = 3;
//                                        break;
//                                    case "ice":
//                                        terrain.typeId = 4;
//                                        break;
//                                    case "white":
//                                        terrain.typeId = 13;
//                                        break;
//                                }
//                            }
//                        }
//                    }
//                }
//
//                data.planets.add(planet);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//}
