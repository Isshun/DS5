package org.smallbox.faraway.core.data.loader;

import org.smallbox.faraway.core.game.model.CharacterTypeInfo;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.util.FileUtils;
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
    private int         _index;

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
        _index = 0;
        data.characters =  new HashMap<>();
        FileUtils.list("data/characters/").stream().filter(file -> file.getName().endsWith(".yml")).forEach(file -> {
            try {
                InputStream input = new FileInputStream(file);
                Yaml yaml = new Yaml(new Constructor(CharacterTypeInfo.class));
                CharacterTypeInfo info = (CharacterTypeInfo) yaml.load(input);
                info.index = _index++;
                info.name = file.getName().replace(".yml", "");
                data.characters.put(info.name, info);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
