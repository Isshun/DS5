package org.smallbox.faraway.model;

import java.util.ArrayList;
import java.util.List;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.model.item.ItemInfo;

public class GameData {

	public List<ItemInfo> 		items;
	public List<ItemInfo> 		gatherItems;
	public List<CategoryInfo> 	categories;
//	public List<Character> 		characters;

	public GameData() {
		gatherItems = new ArrayList<ItemInfo>();
		items = new ArrayList<ItemInfo>();
		categories = new ArrayList<CategoryInfo>();
	}
	
	public ItemInfo getItemInfo(String name) {
		for (ItemInfo info: items) {
			if (info.name.equals(name)) {
				return info;
			}
		}

		// TODO
		return Game.getData().items.get(0);
	}

	public ItemInfo getRandomGatherItem() {
		if (Game.getData().gatherItems.size() > 0) {
			return gatherItems.get((int)(Math.random() * Game.getData().gatherItems.size()));
		}
		return null;
	}

}
