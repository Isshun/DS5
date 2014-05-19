package alone.in.deepspace.model;

import java.util.List;

import alone.in.deepspace.manager.ServiceManager;

public class GameData {

	public List<ItemInfo> 		items;
	public List<CategoryInfo> 	categories;

	public ItemInfo getItemInfo(String name) {
		for (ItemInfo info: items) {
			if (info.name.equals(name)) {
				return info;
			}
		}

		// TODO
		return ServiceManager.getData().items.get(0);
	}

}
