package org.smallbox.faraway.model;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.ui.LayoutModel;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData {

    public static GameData      _data;

	private Map<String, String> _strings;
	public static GameConfig 		    config;
	public List<ItemInfo> 		items;
	public List<ItemInfo> 		gatherItems;
	public List<CategoryInfo> 	categories;
	public List<PlanetModel> 	planets;
    private long                _lastConfigModified;
//	public List<Character> 		characters;

	public GameData() {
		loadStrings();
        loadConfig();
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
		if (Game.getData().gatherItems.size() > 0) {
			return gatherItems.get((int)(Math.random() * Game.getData().gatherItems.size()));
		}
		return null;
	}

	public void loadStrings() {
		try {
			_strings = new HashMap<>();
			File file = new File("data/strings/fr.txt");
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

    public static void setData(GameData data) {
        _data = data;
    }

    public void reloadConfig() {
        long lastConfigModified = new File("data/config.yml").lastModified();
        if (lastConfigModified > _lastConfigModified) {
            Game.getData().loadStrings();
            _lastConfigModified = lastConfigModified;
            loadConfig();
        }
    }

    private void loadConfig() {
        try {
            InputStream input = new FileInputStream(new File("data/config.yml"));
            Yaml yaml = new Yaml(new Constructor(GameConfig.class));
            config = (GameConfig)yaml.load(input);
            System.out.println("Config loaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
