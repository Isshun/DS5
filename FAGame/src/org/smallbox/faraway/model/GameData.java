package org.smallbox.faraway.model;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.model.item.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class GameData {

	public List<ItemInfo> 		items;
	public List<ItemInfo> 		gatherItems;
	public List<CategoryInfo> 	categories;
	public List<PlanetModel> 	planets;
//	public List<Character> 		characters;

	public GameData() {
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

}
