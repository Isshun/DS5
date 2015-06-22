package org.smallbox.faraway.game.model;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.data.loader.*;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.planet.PlanetInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData {
    public static GameData      		_data;
	public static GameConfig 			config;
	public List<ItemInfo> 				items;
	public List<BuffModel> 				buffs;
	public List<ItemInfo> 				gatherItems;
	public List<CategoryInfo> 			categories;
	public List<ItemInfo> 				equipments;
	public List<PlanetInfo> 			planets;
    public Map<String, WeatherModel> 	weathers;
	public HashMap<String, String> 		strings;
	public boolean 						needUIRefresh;
	public List<IDataLoader> 			_loaders;

	public GameData() {
        _data = this;

		_loaders = new ArrayList<>();

		_loaders.add(new ConfigLoader());
		_loaders.add(new BuffLoader());
		_loaders.add(new WeatherLoader());
		_loaders.add(new EquipmentLoader());
		_loaders.add(new StringLoader());
		_loaders.add(new ItemLoader());
		_loaders.add(new PlanetLoader());
		_loaders.add(new CategoryLoader());

		_loaders.forEach(dataLoader -> dataLoader.load(this));
	}

	public void onDataLoaded() {
		//Application.getInstance().refreshConfig();
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

	public String getString(String string) {
		String str = strings.get(string);
		if (str != null) {
			return str;
		}
		return string;
	}

    public static GameData getData() {
        return _data;
    }

    public void reloadConfig() {
		_loaders.forEach(dataLoader -> dataLoader.reloadIfNeeded(this));
    }

	public ItemInfo getEquipment(String name) {
		for (ItemInfo equipment: equipments) {
			if (equipment.name.equals(name)) {
				return equipment;
			}
		}
		return null;
	}
}
