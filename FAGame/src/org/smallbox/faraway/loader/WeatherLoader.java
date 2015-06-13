package org.smallbox.faraway.loader;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.GameDataListener;
import org.smallbox.faraway.model.WeatherModel;
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
public class WeatherLoader {
    private final GameDataListener _listener;
    private long                   _lastConfigModified;

    public WeatherLoader(GameDataListener listener) {
        _listener = listener;
    }

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

        _listener.onDataLoaded();

        Log.debug("Weather loaded");
    }

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
