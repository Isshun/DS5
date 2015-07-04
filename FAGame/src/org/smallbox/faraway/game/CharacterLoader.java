package org.smallbox.faraway.game;

import org.smallbox.faraway.data.loader.IDataLoader;
import org.smallbox.faraway.game.model.CharacterTypeInfo;
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
 * Created by Alex on 03/07/2015.
 */
public class CharacterLoader implements IDataLoader {
    private long        _lastConfigModified;

    @Override
    public void reloadIfNeeded(GameData data) {
        for (File file: new File("data/characters/").listFiles()) {
            if (file.lastModified() > _lastConfigModified) {
                _lastConfigModified = file.lastModified();
                load(data);
                return;
            }
        }
    }

    @Override
    public void load(GameData data) {
        data.characters =  new HashMap<>();
        for (File file: new File("data/characters/").listFiles()) {
            if (file.getName().endsWith(".yml")) {
                try {
                    InputStream input = new FileInputStream(file);
                    Yaml yaml = new Yaml(new Constructor(CharacterTypeInfo.class));
                    CharacterTypeInfo info = (CharacterTypeInfo) yaml.load(input);
                    info.name = file.getName().replace(".yml", "");
                    data.characters.put(info.name, info);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        data.onDataLoaded();

        Log.debug("Character loaded");
    }
}
