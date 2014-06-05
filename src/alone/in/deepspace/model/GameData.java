package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.item.ItemInfo;

public class GameData {

	public List<ItemInfo> 		items;
	public List<ItemInfo> 		gatherItems;
	public List<CategoryInfo> 	categories;
//	public List<Character> 		characters;

	public GameData() {
		gatherItems = new ArrayList<ItemInfo>();
		items = new ArrayList<ItemInfo>();
//		characters = new ArrayList<Character>();
	}
	
	public ItemInfo getItemInfo(String name) {
		for (ItemInfo info: items) {
			if (info.name.equals(name)) {
				return info;
			}
		}

		// TODO
		return ServiceManager.getData().items.get(0);
	}

	public ItemInfo getRandomGatherItem() {
		if (ServiceManager.getData().gatherItems.size() > 0) {
			return gatherItems.get((int)(Math.random() * ServiceManager.getData().gatherItems.size()));
		}
		return null;
	}

}
