package org.smallbox.faraway.data.loader;

import org.smallbox.faraway.game.model.BuffModel;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.util.Log;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Alex on 18/06/2015.
 */
public class BuffLoader implements IDataLoader {
    @Override
    public void load(GameData data) {
        data.buffs = new ArrayList<>();
        try {
            for (File file: new File("data/buffs/").listFiles()) {
                if (file.getName().endsWith(".yml")) {
                    Log.debug("Load active: " + file.getName());
                    InputStream input = new FileInputStream(file);
                    Yaml yaml = new Yaml(new Constructor(BuffModel.class));
                    BuffModel buff = (BuffModel)yaml.load(input);
                    buff.name = file.getName().replace(".yml", "");
                    data.buffs.add(buff);
                }
            }
            for (BuffModel buff: data.buffs) {
                for (BuffModel.BuffLevelModel level: buff.levels) {
                    level.index = buff.levels.indexOf(level);
                }
            }
            Log.info("Buffs loaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadIfNeeded(GameData data) {
    }
}
