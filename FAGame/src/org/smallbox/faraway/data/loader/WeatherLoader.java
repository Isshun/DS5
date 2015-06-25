package org.smallbox.faraway.data.loader;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.WeatherModel;
import org.smallbox.faraway.util.Log;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Alex on 05/06/2015.
 */
public class WeatherLoader implements IDataLoader {
    private long    _lastConfigModified;

    @Override
    public void load(GameData data) {
        data.weathers = new HashMap<>();

        for (File file: new File("data/weather/").listFiles()) {
            if (file.getName().endsWith(".yml")) {
                try {
                    InputStream input = new FileInputStream(file);
                    Yaml yaml = new Yaml(new Constructor(WeatherModel.class));
                    WeatherModel model = (WeatherModel) yaml.load(input);
                    model.name = file.getName().replace(".yml", "");
                    data.weathers.put(model.name, model);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        data.onDataLoaded();

        Log.debug("Weather loaded");
    }

    @Override
    public void reloadIfNeeded(GameData data) {
        for (File file: new File("data/weather/").listFiles()) {
            if (file.lastModified() > _lastConfigModified) {
                _lastConfigModified = file.lastModified();
                load(data);
                return;
            }
        }
    }
}
