package org.smallbox.faraway.core.data.loader;

import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.util.Log;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Alex on 18/06/2015.
 */
public class ConfigLoader implements IDataLoader {
    private long    _lastConfigModified;

    @Override
    public void load(GameData data) {
        try {
            InputStream input = new FileInputStream(new File("data/config.yml"));
            Yaml yaml = new Yaml(new Constructor(GameConfig.class));
            data.config = (GameConfig)yaml.load(input);
            Log.info("Config loaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadIfNeeded(GameData data) {
        long lastConfigModified = new File("data/config.yml").lastModified();
        if (lastConfigModified > _lastConfigModified) {
            load(data);
            _lastConfigModified = lastConfigModified;
        }
    }
}
