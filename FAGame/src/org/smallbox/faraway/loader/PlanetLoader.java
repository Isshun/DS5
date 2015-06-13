package org.smallbox.faraway.loader;

import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.PlanetModel;
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
public class PlanetLoader {
    public static void load(GameData data) {
        try {
            List<PlanetModel> planets = new ArrayList<>();

            for (File file: new File("data/planets/").listFiles()) {
                if (file.getName().endsWith(".yml")) {
                    InputStream input = new FileInputStream(file);
                    Yaml yaml = new Yaml(new Constructor(PlanetModel.class));
                    planets.add((PlanetModel) yaml.load(input));
                }
            }

            data.planets = planets;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
