package org.smallbox.faraway.core.data.loader;

import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.util.Log;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 18/06/2015.
 */
public class EquipmentLoader implements IDataLoader {

    @Override
    public void reloadIfNeeded(GameData data) {
    }

    @Override
    public void load(GameData data) {
        data.equipments = new ArrayList<>();
        try {
            loadDirectory(data.equipments, new File("data/items/equipments/"));
            Log.info("Equipments loaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDirectory(List<ItemInfo> equipments, File directory) throws FileNotFoundException {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    loadDirectory(equipments, file);
                }
                if (file.getName().endsWith(".yml")) {
                    loadEquipment(equipments, file);
                }
            }
        }
    }

    private void loadEquipment(List<ItemInfo> equipments, File file) throws FileNotFoundException {
        Log.debug("Load equipment: " + file.getName());
        InputStream input = new FileInputStream(file);
        Yaml yaml = new Yaml(new Constructor(ItemInfo.class));
        ItemInfo equipment = (ItemInfo)yaml.load(input);
        equipment.name = "base.equipments." + file.getName().replace(".yml", "");

        if (equipment.equipment.location == null) {
            Log.error("Equipment has no location: " + equipment.name);
            return;
        }

        equipments.add(equipment);
    }
}
