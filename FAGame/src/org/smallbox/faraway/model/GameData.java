package org.smallbox.faraway.model;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.loader.WeatherLoader;
import org.smallbox.faraway.model.item.ItemInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.*;

public class GameData implements GameDataListener {

	@Override
    public void onDataLoaded() {
        Application.getInstance().refreshConfig();
    }

    public static GameData      _data;
    private final WeatherLoader _weatherLoader;

    private Map<String, String> _strings;
	public static GameConfig 	config;
	public List<ItemInfo> 		items;
	public List<BuffModel> 		buffs;
	public List<ItemInfo> 		gatherItems;
	public List<CategoryInfo> 	categories;
	public List<EquipmentModel> equipments;
	public List<PlanetModel> 	planets;
    private long                _lastConfigModified;
    public Map<String, WeatherModel> weathers;

	public GameData() {
        _data = this;

        _weatherLoader = new WeatherLoader(this);
        _weatherLoader.load(this);

        loadConfig();
		loadStrings(GameData.config.lang);
        loadBuffs();
        loadEquipments();
        gatherItems = new ArrayList<>();
		items = new ArrayList<>();
		categories = new ArrayList<>();
	}

	public ItemInfo getItemInfo(String name) {
		for (ItemInfo info: items) {
			if (info.name.equals(name)) {
				return info;
			}
		}

		throw new RuntimeException("item not exists: " + name);
	}

	public ItemInfo getRandomGatherItem() {
		if (gatherItems.size() > 0) {
			return gatherItems.get((int)(Math.random() * gatherItems.size()));
		}
		return null;
	}

	public void loadStrings(String lang) {
		try {
			_strings = new HashMap<>();
			File file = new File("data/strings/" + lang + ".txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains(":") && !line.startsWith("#")) {
					int sep = line.indexOf(':');
					if (line.contains("\"")) {
						sep = line.indexOf(':', line.indexOf('"', line.indexOf('"')+1)+1);
					}

					String key = line.substring(0, sep).trim().replace("\"", "");
					String value  = line.substring(sep + 1).trim().replace("\"", "");
					_strings.put(key, value);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getString(String string) {
		String str = _strings.get(string);
		if (str != null) {
			return str;
		}
		return string;
	}

    public static GameData getData() {
        return _data;
    }

    public void reloadConfig() {
        if (_weatherLoader != null) {
            _weatherLoader.reloadIfNeeded(this);
        }

        long lastConfigModified = new File("data/config.yml").lastModified();
        if (lastConfigModified > _lastConfigModified) {
			loadStrings(GameData.config.lang);
            _lastConfigModified = lastConfigModified;
            loadConfig();
        }
    }

    private void loadConfig() {
        try {
            InputStream input = new FileInputStream(new File("data/config.yml"));
            Yaml yaml = new Yaml(new Constructor(GameConfig.class));
            config = (GameConfig)yaml.load(input);
            Log.info("Config loaded");
            onDataLoaded();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

	private void loadBuffs() {
		buffs = new ArrayList<>();
		try {
			for (File file: new File("data/buffs/").listFiles()) {
				if (file.getName().endsWith(".yml")) {
					Log.debug("Load active: " + file.getName());
					InputStream input = new FileInputStream(file);
					Yaml yaml = new Yaml(new Constructor(BuffModel.class));
                    BuffModel buff = (BuffModel)yaml.load(input);
                    buff.name = file.getName().replace(".yml", "");
					buffs.add(buff);
				}
			}
            for (BuffModel buff: buffs) {
                for (BuffModel.BuffLevelModel level: buff.levels) {
                    level.index = buff.levels.indexOf(level);
                }
            }
//            Collections.sort(buffs, (b1, b2) -> b2.effects.mood - b1.effects.mood);
            Log.info("Buffs loaded");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void loadEquipments() {
        equipments = new ArrayList<>();
		try {
			for (File file: new File("data/equipments/").listFiles()) {
				if (file.getName().endsWith(".yml")) {
					Log.debug("Load equipment: " + file.getName());
					InputStream input = new FileInputStream(file);
					Yaml yaml = new Yaml(new Constructor(EquipmentModel.class));
					EquipmentModel equipment = (EquipmentModel)yaml.load(input);
					equipment.name = "base.equipments." + file.getName().replace(".yml", "");

                    if (equipment.location == null) {
                        Log.error("Equipment has no location: " + equipment.name);
                        break;
                    }

                    equipments.add(equipment);
				}
			}
			for (BuffModel buff: buffs) {
				for (BuffModel.BuffLevelModel level: buff.levels) {
					level.index = buff.levels.indexOf(level) + 1;
				}
			}
//            Collections.sort(buffs, (b1, b2) -> b2.effects.mood - b1.effects.mood);
			Log.info("Buffs loaded");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public EquipmentModel getEquipment(String name) {
		for (EquipmentModel equipment: equipments) {
			if (equipment.name.equals(name)) {
				return equipment;
			}
		}
		return null;
	}
}
